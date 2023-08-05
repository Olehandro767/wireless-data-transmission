package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.edu.ontu.wdt.EmptyUiObserver
import ua.edu.ontu.wdt.layer.*
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.SendFileProgressDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpUtils
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener.TcpDeviceRequestListener
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

class TcpSendFileRequestBuilder(
    private val logger: ILog,
    private val context: IContext,
    private val messageHandler: IRequestResponseHandler,
    private val getInfoConfiguration: IGetInfoRequest,
    private val tcpDeviceRequestListener: TcpDeviceRequestListener,
    private val onStartObserver: IUiGenericObserver<GetInfoDto> = EmptyUiObserver(),
    private val progressUiObserver: IUiGenericObserver<SendFileProgressDto> = EmptyUiObserver(),
    private val onFinishObserver: IUiObserver = EmptyUiObserver<Any>(),
    private val onProblemObserver: IUiGenericObserver<String> = EmptyUiObserver()
): ISendFileRequestBuilder {

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

        "{${files}f${foldersSize}d}"
    } else if (files.isNotEmpty())
        files[0].name
    else
        throw IllegalArgumentException("Add files")

    private fun sendFile(
        file: File,
        messageSender: DataOutputStream,
        numberOfFiles: Int,
        sentFiles: AtomicInteger,
        rootFolderName: String?
    ) {
        val buffer = ByteArray(this.context.dataBufferSize)
        val fileInputStream = FileInputStream(file)
        var transferredBytes = 0
        val fileLength = file.length()
        var transferredLength = 0

        if (fileLength > 0) {
            messageSender.writeBoolean(rootFolderName != null)

            if (rootFolderName != null) {
                val sendPath = file.absolutePath.substring(file.absolutePath.lastIndexOf(rootFolderName))
                messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(sendPath))
            }

            messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend("${file.name},${file.length()}"))

            while (this.tcpDeviceRequestListener.isRun.get() && fileInputStream.read(buffer).also { transferredBytes = it } > 0) {
                transferredLength += transferredBytes

                if (transferredLength >= fileLength) {
                    messageSender.write(this.messageHandler.handleAcceptedBytes(buffer), 0, transferredBytes)
                    messageSender.flush()
                    this.progressUiObserver.notifyUi(SendFileProgressDto(fileLength, file.name, file.path, 100))
                    break
                }

                messageSender.write(this.messageHandler.handleAcceptedBytes(buffer), 0, transferredBytes)
                messageSender.flush()
                this.progressUiObserver.notifyUi(
                    SendFileProgressDto(
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
        }
    }

    private suspend fun sendFolder(
        file: File,
        messageSender: DataOutputStream,
        numberOfFiles: Int,
        sentFiles: AtomicInteger,
        rootFolderName: String?
    ) {
        val childFiles = file.listFiles()

        if (childFiles != null) {
            for (item in childFiles) {
                if (item.isDirectory) {
                    this.sendFolder(item, messageSender, numberOfFiles, sentFiles, rootFolderName)
                } else {
                    this.sendFile(item, messageSender, numberOfFiles, sentFiles, rootFolderName)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun doRequest(ip: String, vararg files: File) {
        this.onStartObserver.notifyUi(this.getInfoConfiguration.doRequest(ip))
        val sentFiles = AtomicInteger(0)
        val socket = Socket(ip, this.context.port)
        val messageSender = TcpUtils.createMessageSender(socket)
        val messageReader = TcpUtils.createMessagesReader(socket)
        val numberOfFiles = this.countFiles(*files)
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend("${files.size},${this.solveNameProblem(*files)}"))
        messageSender.flush()

        if (messageReader.readBoolean()) {
            for (item in files) {
                if (item.isDirectory) {
                    GlobalScope.launch {
                        sendFolder(
                            item,
                            messageSender,
                            numberOfFiles,
                            sentFiles,
                            item.absolutePath.substring(item.absolutePath.indexOf(item.name))
                        )
                    }
                } else {
                    this.sendFile(item, messageSender, numberOfFiles, sentFiles, null)
                }
            }
        } else {
            this.onProblemObserver.notifyUi("not_accepted")
        }
    }
}