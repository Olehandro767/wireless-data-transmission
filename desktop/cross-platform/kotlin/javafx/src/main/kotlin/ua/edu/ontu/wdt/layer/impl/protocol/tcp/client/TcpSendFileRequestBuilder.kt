package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import kotlinx.coroutines.*
import ua.edu.ontu.wdt.layer.*
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.SEND_FILES_OR_FOLDERS
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpUtils
import ua.edu.ontu.wdt.layer.utils.request.RequestUtils.prepareRequestType
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TcpSendFileRequestBuilder(
    private val logger: ILog,
    private val context: IContext,
    private val messageHandler: IRequestResponseHandler,
    private val getInfoConfiguration: IGetInfoRequest,
    private val onStartObserver: IUiGenericObserver<GetInfoDto>,
    private val progressUiObserver: IUiGenericObserver<FileProgressDto>,
    private val onFinishObserver: IUiObserver,
    private val onCancelObserver: IUiGenericObserver<AtomicBoolean>,
    private val onProblemObserver: IUiGenericObserver<String>,
): ISendFileRequestBuilder {

    private val random: Random = Random()
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private fun countFiles(vararg files: File): Int {
        var size = 0
        for (item in files) {
            if (item.isDirectory) {
                val dirFiles = item.listFiles()

                if (dirFiles != null) {
                    size += this.countFiles(*dirFiles)
                }
            } else {
                size++
            }
        }
        return size
    }

    private fun solveNameProblem(vararg files: File): String = if (files.size > 1) {
        var filesSize = 0
        var foldersSize = 0

        for (item in files) {
            if (item.isDirectory) {
                foldersSize++
            } else {
                filesSize++
            }
        }

        "${filesSize}{./*}${foldersSize}"
    } else if (files.isNotEmpty()) {
        files[0].name
    } else {
        throw IllegalArgumentException("Add files")
    }

    private fun sendFile(
        serverSocket: ServerSocket,
        file: File,
        messageSender: DataOutputStream,
        numberOfFiles: Int,
        sentFiles: AtomicInteger,
        isRunning: AtomicBoolean,
        rootFolderName: String?
    ) {
        val buffer = ByteArray(this.context.dataBufferSize)
        val fileInputStream = FileInputStream(file)
        var transferredBytes = 0
        val fileLength = file.length()
        var transferredLength = 0
        messageSender.writeBoolean(fileLength > 0)
        messageSender.flush()

        if (fileLength > 0) {
            messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(if (rootFolderName != null) {
                "${file.absolutePath.substring(file.absolutePath.lastIndexOf(rootFolderName))}/${file.name},${file.length()}"
            } else {
                "${file.name},${file.length()}"
            }))
            messageSender.flush()

            while (isRunning.get() && fileInputStream.read(buffer).also { transferredBytes = it } > 0) {
                transferredLength += transferredBytes

                if (transferredLength >= fileLength) {
                    messageSender.write(this.messageHandler.handleAcceptedBytes(buffer), 0, transferredBytes)
                    messageSender.flush()
                    this.progressUiObserver.notifyUi(FileProgressDto(fileLength, file.name, file.path, 100))
                    break
                }

                messageSender.write(this.messageHandler.handleAcceptedBytes(buffer), 0, transferredBytes)
                messageSender.flush()
                this.progressUiObserver.notifyUi(
                    FileProgressDto(
                        fileLength,
                        file.name,
                        file.path,
                        ((transferredLength / fileLength) * 100).toByte()
                    )
                )
            }
        }

        sentFiles.set(sentFiles.get() + 1)

        if (sentFiles.get() == numberOfFiles) {
            this.onFinishObserver.notifyUi()
            serverSocket.close()
        }
    }

    private suspend fun sendFolder(
        serverSocket: ServerSocket,
        file: File,
        messageSender: DataOutputStream,
        numberOfFiles: Int,
        sentFiles: AtomicInteger,
        rootFolderName: String,
        isRunning: AtomicBoolean
    ) {
        val childFiles = file.listFiles()
        withContext(this.ioDispatcher) {
            if (childFiles != null) {
                messageSender.writeUTF(messageHandler.handleMessageBeforeSend(file.absolutePath.substring(
                    file.absolutePath.indexOf(rootFolderName)
                )))
                messageSender.flush()

                for (item in childFiles) {
                    messageSender.writeBoolean(item.isDirectory)
                    messageSender.flush()

                    if (item.isDirectory) {
                        sendFolder(serverSocket, item, messageSender, numberOfFiles, sentFiles, rootFolderName, isRunning)
                    } else {
                        sendFile(serverSocket, item, messageSender, numberOfFiles, sentFiles, isRunning, rootFolderName)
                    }
                }
            } else {
                messageSender.writeUTF(messageHandler.handleMessageBeforeSend("{}/*IGNORE_THIS_FOLDER/*{}"))
                messageSender.flush()
            }
        }
    }

    private suspend fun sendData(
        serverSocket: ServerSocket,
        file: File,
        socket: Socket,
        numberOfFiles: Int,
        infoWithToken: String,
        sentFiles: AtomicInteger,
        isRunning: AtomicBoolean,
    ) {
        this.logger.info("Prepare: ${file.name}")
        val messageReader = TcpUtils.createMessagesReader(socket)
        val messageSender = TcpUtils.createMessageSender(socket)
        withContext(this.ioDispatcher) {
            messageSender.writeBoolean(isRunning.get())
            messageSender.flush()

            if (messageReader.readBoolean()) {
                if (messageHandler.handleAcceptedMessage(messageReader.readUTF()) == infoWithToken) {
                    messageSender.writeBoolean(true)
                    messageSender.flush()

                    if (file.isDirectory) {
                        logger.info("Sending dir(${file.name}) in new thread")
                        messageSender.writeUTF(messageHandler.handleMessageBeforeSend("folder"))
                        messageSender.flush()
                        sendFolder(
                            serverSocket,
                            file,
                            messageSender,
                            numberOfFiles,
                            sentFiles,
                            file.absolutePath.substring(file.absolutePath.indexOf(file.name)),
                            isRunning
                        )
                    } else {
                        messageSender.writeUTF(messageHandler.handleMessageBeforeSend("file"))
                        messageSender.flush()
                        sendFile(serverSocket, file, messageSender, numberOfFiles, sentFiles, isRunning, null)
                    }
                } else {
                    messageSender.writeBoolean(false)
                    messageSender.flush()
                    onProblemObserver.notifyUi("invalid_device")
                }
            } else {
                onProblemObserver.notifyUi("canceled_file_accepting")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class) // in future try: 1. create all folders 2. send files
    override fun doRequest(ip: String, vararg files: File) {
        this.onStartObserver.notifyUi(this.getInfoConfiguration.doRequest(ip))
        val sentFiles = AtomicInteger(0)
        val socket = Socket(ip, this.context.port)
        val messageSender = TcpUtils.createMessageSender(socket)
        val messageReader = TcpUtils.createMessagesReader(socket)
        val numberOfFiles = this.countFiles(*files)
        val infoWithToken = "${files.size},$numberOfFiles,${this.solveNameProblem(*files)},${this.random.nextInt(10000, 999999)}"
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(prepareRequestType(SEND_FILES_OR_FOLDERS)))
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(infoWithToken))
        messageSender.flush()
        this.logger.info("Accept File: Wait on response")

        if (messageReader.readBoolean()) {
            val serverSocket = ServerSocket(this.context.asyncPort)
            val isRunning = AtomicBoolean(true)
            this.onCancelObserver.notifyUi(isRunning)

            for (item in files) {
                serverSocket.accept().let {
                    logger.info("Send file: ${item.name}")
                    runBlocking { run {
                        sendData(serverSocket, item, it, numberOfFiles, infoWithToken, sentFiles, isRunning)
                    } }
                }
            }
        } else {
            this.onProblemObserver.notifyUi("not_accepted")
        }
    }
}