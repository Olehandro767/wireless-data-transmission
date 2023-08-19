package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import kotlinx.coroutines.*
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.SEND_FILES_OR_FOLDERS
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.IRequest
import ua.edu.ontu.wdt.layer.client.ISendFileRequestBuilder
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpCommon.FILE_DELIMITER
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpIOFactory
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserver
import ua.edu.ontu.wdt.layer.utils.ArrayUtils
import ua.edu.ontu.wdt.layer.utils.RequestUtils.prepareRequestType
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private data class TempFileData(val file: File, val rootFolderName: String? = null)

class TcpMultiSendFileRequestBuilder( // TODO review
        private val logger: ILog,
        private val context: IContext,
        private val messageHandler: IIOSecurityHandler,
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

        "${filesSize}${FILE_DELIMITER}${foldersSize}"
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
        val fileLength = file.length()
        val bufferSize = if (this.context.dataBufferSize < file.length()) this.context.dataBufferSize else file.length().toInt()
        val buffer = ByteArray(bufferSize)
        val fileInputStream = FileInputStream(file)
        var transferredBytes = 0
        var transferredLength = 0
        messageSender.writeBoolean(fileLength > 0)
        messageSender.flush()

        if (fileLength > 0) {
            messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(if (rootFolderName != null) {
                "${file.absolutePath.substring(file.absolutePath.lastIndexOf(rootFolderName))},${file.length()}"
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

    private suspend fun checkTokenAndSendFile(
            socket: Socket,
            serverSocket: ServerSocket,
            chunk: List<TempFileData>,
            infoWithToken: String,
            numberOfFiles: Int,
            sentFiles: AtomicInteger,
            isRunning: AtomicBoolean,
    ) {
        withContext(this.ioDispatcher) {
            val serverMessageSender = TcpIOFactory.createMessageSender(socket)
            val serverMessageReader = TcpIOFactory.createMessagesReader(socket)

            // check token
            if (messageHandler.handleAcceptedMessage(serverMessageReader.readUTF()) == infoWithToken) {
                serverMessageSender.writeBoolean(true)
                serverMessageSender.writeInt(chunk.size)
                serverMessageSender.flush()
                for (file in chunk) {
                    sendFile(serverSocket, file.file, serverMessageSender, numberOfFiles, sentFiles, isRunning, file.rootFolderName)
                }
            } else {
                serverMessageSender.writeBoolean(false)
                serverMessageSender.flush()
                onProblemObserver.notifyUi("token_error")
            }
        }
    }

    private fun prepareFoldersOnClient(
            file: File,
            innerFiles: Array<File>?,
            fileArray: ArrayList<TempFileData>,
            messageSender: DataOutputStream,
            rootFolderName: String
    ) {
        if (!innerFiles.isNullOrEmpty()) {
            val folderPath = file.absolutePath.substring(file.absolutePath.indexOf(rootFolderName))
            // prepare folder on client
            messageSender.writeBoolean(true)
            messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(folderPath))
            messageSender.flush()

            for (item in innerFiles) {
                if (item.isDirectory) {
                    this.prepareFoldersOnClient(item, item.listFiles(), fileArray, messageSender, rootFolderName)
                } else {
                    fileArray.add(TempFileData(item, rootFolderName))
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun doRequest(ip: String, vararg files: File) {
        val remountDeviceInfo = this.getInfoConfiguration.doRequest(ip)
        this.onStartObserver.notifyUi(remountDeviceInfo)
        val socket = Socket(ip, this.context.port)
        val messageSender = TcpIOFactory.createMessageSender(socket)
        val messageReader = TcpIOFactory.createMessagesReader(socket)
        val numberOfFiles = this.countFiles(*files)
        val infoWithToken = "${files.size},$numberOfFiles,${this.solveNameProblem(*files)},${this.random.nextInt(10000, 999999)}"
        // send request type
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(prepareRequestType(SEND_FILES_OR_FOLDERS)))
        // send files info with token
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(infoWithToken))
        messageSender.flush()
        this.logger.info("Accept File: Wait on response")

        // check on accepted request
        if (messageReader.readBoolean()) {
            val sentFiles = AtomicInteger(0)
            val fileArray = ArrayList<TempFileData>()
            val isRunning = AtomicBoolean(true)
            val serverSocket = ServerSocket(this.context.asyncPort)
            this.onCancelObserver.notifyUi(isRunning)

            serverSocket.accept().let {
                val serverMessageSender = TcpIOFactory.createMessageSender(it)
                val serverMessageReader = TcpIOFactory.createMessagesReader(it)

                // check token
                if (this.messageHandler.handleAcceptedMessage(serverMessageReader.readUTF()) == infoWithToken) {
                    serverMessageSender.writeBoolean(true)
                    serverMessageSender.flush()
                    for (item in files) {
                        if (item.exists()) {
                            if (item.isDirectory) {
                                this.prepareFoldersOnClient(item, item.listFiles(), fileArray, serverMessageSender, item.name)
                            } else {
                                fileArray.add(TempFileData(item))
                            }
                        }
                    }
                    // finish flag about sending files
                    serverMessageSender.writeBoolean(false)
                    serverMessageSender.flush()
                } else {
                    // token error
                    serverMessageSender.writeBoolean(false)
                    serverMessageSender.flush()
                    this.onProblemObserver.notifyUi("token_error")
                    return
                }

                // check accepted info
                if (!serverMessageReader.readBoolean()) {
                    this.onProblemObserver.notifyUi("smth_wrong")
                    return
                }

                it.close()
            }

            val splitFileArray = ArrayUtils.splitArrayWithConsideration(
                    fileArray, this.context.maxThreadsForSending.coerceAtMost(remountDeviceInfo.maxThreadsForSending))
            // number of threads
            messageSender.writeInt(splitFileArray.size)
            messageSender.flush()

            for (chunk in splitFileArray) {
                serverSocket.accept().let {
                    GlobalScope.launch {
                        checkTokenAndSendFile(it, serverSocket, chunk, infoWithToken, numberOfFiles, sentFiles, isRunning)
                        it.close()
                    }
                }
            }
        } else {
            this.onProblemObserver.notifyUi("not_accepted")
        }
    }

    override fun build(): IRequest {
        TODO("Not yet implemented")
    }

    override fun ip(ip: String): ISendFileRequestBuilder {
        TODO("Not yet implemented")
    }

    override fun files(vararg files: File): ISendFileRequestBuilder {
        TODO("Not yet implemented")
    }
}
