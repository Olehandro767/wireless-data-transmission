package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.RequestDto
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageReader
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageSender
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpNativeChunkedDataReader
import ua.edu.ontu.wdt.layer.ui.IUiGenericConfirmMessage
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.utils.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class TcpLegacyAcceptFileService(
    // single thread
    private val logger: ILog,
    private val context: IContext,
    private val asyncConfiguration: IAsyncConfiguration,
    private val onEnd: ITcpLambda,
    private val messageHandler: IIOSecurityHandler,
    private val confirmFileMessage: IUiGenericConfirmMessage<ConfirmFileDto>,
    private val progressUiObserver: IUiGenericObserver<FileProgressDto>,
    private val onCancelObserver: IUiGenericObserver<AtomicBoolean>,
    private val onStartObserver: IUiGenericObserver<GetInfoDto>,
    private val onProblemObserver: IUiGenericObserver<String>,
) : ITcpLambda {

    private fun acceptFile(socket: Socket, messageReader: TcpMessageReader, messageSender: TcpMessageSender) {
        val fileInfo = messageReader.readMessageFromRemoteDevice().split(',')
        val fileLength = fileInfo[1].toLong()
        val path = "${this.context.downloadFolderPath}/${fileInfo[0]}"
        val bufferSize =
            if (this.context.dataBufferSize < fileLength) this.context.dataBufferSize else fileLength.toInt()
        val nativeChunkedFileReader =
            TcpNativeChunkedDataReader(socket, bufferSize, this.messageHandler, messageReader.toInputStream())
        val file = File(path)
        val fileOutputStream = FileOutputStream(file)
        var fileAcceptedLength = 0L
        file.createNewFile() // TODO check
        this.logger.info(path)

        while (fileLength != fileAcceptedLength && nativeChunkedFileReader.readChunk().readDataSize > 0) {
            fileAcceptedLength += nativeChunkedFileReader.readDataSize
            fileOutputStream.write(nativeChunkedFileReader.buffer, 0, nativeChunkedFileReader.readDataSize)
            this.logger.info("Accept File Service: accepted chunk")
            this.progressUiObserver.notifyUi(
                FileProgressDto(
                    fileLength,
                    file.name,
                    file.absolutePath,
                    ((fileAcceptedLength / fileLength) * 100).toByte()
                )
            )
        }

        messageSender.sendMessageToRemoteDevice(true)
    }

    override fun invoke(request: RequestDto<Socket>) {
        val remoteDeviceInfo =
            DeviceRequestAbstractFactory.createDeviceRequestFactory(this.context, this.asyncConfiguration)
                .createGetInfoRequestBuilder().doRequest(request.context.inetAddress.hostAddress)
        this.onStartObserver.notifyUi(remoteDeviceInfo)
        val messageReader = TcpMessageReader(request.context, this.messageHandler)
        val messageSender = TcpMessageSender(request.context, this.messageHandler)
        val infoWithToken = FileUtils.parseToFileRequestInfoDto(messageReader.readMessageFromRemoteDevice())
        var acceptedFiles = 0
        this.confirmFileMessage.ask(
            ConfirmFileDto(
                remoteDeviceInfo,
                infoWithToken.filesNumber,
                infoWithToken.foldersNumber,
                infoWithToken.title
            ),
            onAccept = {
                try {
                    logger.info("Accepted request")
                    val isRunning = AtomicBoolean(true)
                    messageSender.sendMessageToRemoteDevice(true)
                    this.onCancelObserver.notifyUi(isRunning)

                    // create folders
                    while (messageReader.readBoolMessageFromRemoteDevice()) {
                        val folderPath = messageReader.readMessageFromRemoteDevice()
                        File("${this.context.downloadFolderPath}/${folderPath}").mkdirs()
                    }

                    messageSender.sendMessageToRemoteDevice(true)

                    // read while files are exists
                    while (messageReader.readBoolMessageFromRemoteDevice()) {
                        this.acceptFile(request.context, messageReader, messageSender)
                        acceptedFiles++
                    }

                    this.onEnd(request)
                } catch (exception: Exception) {
                    exception.message?.let { this.onProblemObserver.notifyUi(it) }
                    messageSender.sendMessageToRemoteDevice(false)
                }
            },
            onCancel = {
                logger.info("Canceled request")
                messageSender.sendMessageToRemoteDevice(false)
                this.onProblemObserver.notifyUi("canceled_request")
                this.onEnd(request)
            }
        )
    }
}