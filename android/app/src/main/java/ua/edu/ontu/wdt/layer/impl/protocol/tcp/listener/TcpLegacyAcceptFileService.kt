package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
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
    private val _logger: ILog,
    private val _onEnd: ITcpLambda,
    private val _messageHandler: IIOSecurityHandler,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
) : ITcpLambda {

    private val _confirmFileMessage: IUiGenericConfirmMessage<ConfirmFileDto> =
        _genericConfiguration.uiConfiguration.createConfirmFileMessage()
    private val _progressUiObserver: IUiGenericObserver<FileProgressDto> =
        _genericConfiguration.uiConfiguration.createProgressObserverForSendFileRule()
    private val _onCancelObserver: IUiGenericObserver<AtomicBoolean> =
        _genericConfiguration.uiConfiguration.createCancelObserver()
    private val _onStartObserver: IUiGenericObserver<GetInfoDto> =
        _genericConfiguration.uiConfiguration.createBeforeSendCommonObserver()
    private val _onProblemObserver: IUiGenericObserver<String> =
        _genericConfiguration.uiConfiguration.createProblemObserverForSendFileRule()

    private fun acceptFile(
        socket: Socket, messageReader: TcpMessageReader, messageSender: TcpMessageSender
    ) {
        val fileInfo = messageReader.readMessageFromRemoteDevice().split(',')
        val fileLength = fileInfo[1].toLong()
        val path = "${_genericConfiguration.context.downloadFolderPath}/${fileInfo[0]}"
        val bufferSize =
            if (_genericConfiguration.context.dataBufferSize < fileLength) _genericConfiguration.context.dataBufferSize else fileLength.toInt()
        val nativeChunkedFileReader = TcpNativeChunkedDataReader(
            socket, bufferSize, this._messageHandler, messageReader.toInputStream()
        )
        val file = File(path)
        val fileOutputStream = FileOutputStream(file)
        var fileAcceptedLength = 0L
        file.createNewFile() // TODO check
        this._logger.info(path)

        while (fileLength != fileAcceptedLength && nativeChunkedFileReader.readChunk().readDataSize > 0) {
            fileAcceptedLength += nativeChunkedFileReader.readDataSize
            fileOutputStream.write(
                nativeChunkedFileReader.buffer, 0, nativeChunkedFileReader.readDataSize
            )
            this._logger.info("Accept File Service: accepted chunk")
            this._progressUiObserver.notifyUi(
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
            DeviceRequestAbstractFactory.createDeviceRequestFactory(_genericConfiguration, _logger)
                .createGetInfoRequestBuilder()
                .doRequest(request.context.inetAddress.hostAddress!!) // TODO check
        _onStartObserver.notifyUi(remoteDeviceInfo)
        val messageReader = TcpMessageReader(request.context, _messageHandler)
        val messageSender = TcpMessageSender(request.context, _messageHandler)
        val infoWithToken =
            FileUtils.parseToFileRequestInfoDto(messageReader.readMessageFromRemoteDevice())
        var acceptedFiles = 0
        this._confirmFileMessage.ask(ConfirmFileDto(
            remoteDeviceInfo,
            infoWithToken.filesNumber,
            infoWithToken.foldersNumber,
            infoWithToken.title
        ), onAccept = {
            try {
                _logger.info("Accepted request")
                val isRunning = AtomicBoolean(true)
                messageSender.sendMessageToRemoteDevice(true)
                this._onCancelObserver.notifyUi(isRunning)

                // create folders
                while (messageReader.readBoolMessageFromRemoteDevice()) {
                    val folderPath = messageReader.readMessageFromRemoteDevice()
                    File("${_genericConfiguration.context.downloadFolderPath}/${folderPath}").mkdirs()
                }

                messageSender.sendMessageToRemoteDevice(true)

                // read while files are exists
                while (messageReader.readBoolMessageFromRemoteDevice()) {
                    this.acceptFile(request.context, messageReader, messageSender)
                    acceptedFiles++
                }

                _onEnd(request)
            } catch (exception: Exception) {
                exception.message?.let { _onProblemObserver.notifyUi(it) }
                messageSender.sendMessageToRemoteDevice(false)
            }
        }, onCancel = {
            _logger.info("Canceled request")
            messageSender.sendMessageToRemoteDevice(false)
            _onProblemObserver.notifyUi("canceled_request")
            _onEnd(request)
        })
    }
}