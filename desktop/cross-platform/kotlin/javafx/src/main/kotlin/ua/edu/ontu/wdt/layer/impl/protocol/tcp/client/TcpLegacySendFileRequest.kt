package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.SEND_FILES_OR_FOLDERS
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.IRequest
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.FileInfoDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageReader
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageSender
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpNativeChunkedDataSender
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserver
import ua.edu.ontu.wdt.layer.utils.FileUtils.generateFileInfo
import ua.edu.ontu.wdt.layer.utils.FileUtils.generateFilesInfoWithToken
import ua.edu.ontu.wdt.layer.utils.FileUtils.parseToFileRequestInfoDto
import ua.edu.ontu.wdt.layer.utils.FileUtils.separateFilesAndFolders
import ua.edu.ontu.wdt.layer.utils.RequestUtils.prepareRequestType
import java.io.File
import java.io.FileInputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class TcpLegacySendFileRequest(
    private val ip: String,
    private val logger: ILog,
    private val context: IContext,
    private val ioHandler: IIOSecurityHandler,
    private val getInfoConfiguration: IGetInfoRequest,
    private val onStartObserver: IUiGenericObserver<GetInfoDto>,
    private val progressUiObserver: IUiGenericObserver<FileProgressDto>,
    private val onFinishObserver: IUiObserver,
    private val onCancelObserver: IUiGenericObserver<AtomicBoolean>,
    private val onProblemObserver: IUiGenericObserver<String>,
    private val files: Array<out File>,
) : IRequest {

    private fun sendFile(
        socket: Socket,
        fileInfo: FileInfoDto,
        messageSender: TcpMessageSender,
        messageReader: TcpMessageReader
    ) {
        // send file info
        messageSender.sendMessageToRemoteDevice(generateFileInfo(fileInfo))
        val fileLength = fileInfo.entity.length()
        val bufferSize =
            if (this.context.dataBufferSize < fileLength) this.context.dataBufferSize else fileLength.toInt()
        val buffer = ByteArray(bufferSize)
        val nativeChunkedFileSender = TcpNativeChunkedDataSender(socket, this.ioHandler, messageSender.toOutputStream())
        val fileInputStream = FileInputStream(fileInfo.entity)
        var transferredBytes: Int
        var transferredLength = 0

        while (fileInputStream.read(buffer).also { transferredBytes = it } > 0) {
            transferredLength += transferredBytes

            if (transferredLength >= fileLength) {
                nativeChunkedFileSender.sendChunkToRemoteDevice(buffer, transferredBytes)
                this.progressUiObserver.notifyUi(
                    FileProgressDto(
                        fileLength,
                        fileInfo.entity.name,
                        fileInfo.entity.path,
                        100
                    )
                )
                break
            }

            nativeChunkedFileSender.sendChunkToRemoteDevice(buffer, transferredBytes)
            this.progressUiObserver.notifyUi(
                FileProgressDto(
                    fileLength,
                    fileInfo.entity.name,
                    fileInfo.entity.path,
                    ((transferredLength / fileLength) * 100).toByte()
                )
            )
        }

        // confirm
        messageReader.readBoolMessageFromRemoteDevice()
    }

    override fun doRequest() {
        try {
            val remoteDeviceInfo = this.getInfoConfiguration.doRequest(this.ip)
            this.onStartObserver.notifyUi(remoteDeviceInfo)
            Socket(ip, this.context.port).let {
                val messageReader = TcpMessageReader(it, this.ioHandler)
                val messageSender = TcpMessageSender(it, this.ioHandler)
                val splitFilesAndFolders = separateFilesAndFolders(*this.files)
                val infoWithToken = parseToFileRequestInfoDto(generateFilesInfoWithToken(splitFilesAndFolders))
                // send info to remote device about files and folders
                messageSender.prepareMessage(prepareRequestType(SEND_FILES_OR_FOLDERS))
                    .prepareMessage(infoWithToken.rawString)
                    .sendToRemoteDevice()
                this.logger.info("Send File: Wait on response...")

                // check on accepted request
                if (messageReader.readBoolMessageFromRemoteDevice()) {
                    var sentFiles = 0
                    val isRunning = AtomicBoolean(true)
                    this.onCancelObserver.notifyUi(isRunning)

                    for (folder in splitFilesAndFolders.folders) {
                        val folderPath = folder.getPathFromRootFolderOrGetEntityName()
                        messageSender.prepareMessage(true).prepareMessage(folderPath).sendToRemoteDevice()
                    }

                    messageSender.sendMessageToRemoteDevice(false)

                    // confirm
                    if (messageReader.readBoolMessageFromRemoteDevice()) {
                        // send files
                        for (file in splitFilesAndFolders.files) {
                            messageSender.prepareMessage(true)
                            this.sendFile(it, file, messageSender, messageReader)
                            sentFiles++
                        }

                        messageSender.prepareMessage(false)
                    }
                }
            }
        } catch (exception: Exception) {
            exception.message?.let { this.onProblemObserver.notifyUi(it) }
        }
    }
}