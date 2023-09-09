package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.RequestDto
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpCommon.FILE_DELIMITER
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpIOFactory
import ua.edu.ontu.wdt.layer.ui.IUiGenericConfirmMessage
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TcpMultiAcceptFileService(
    // TODO
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
        _genericConfiguration.uiConfiguration.createCancelObserverForSendFileRule()
    private val _onStartObserver: IUiGenericObserver<GetInfoDto> =
        _genericConfiguration.uiConfiguration.createBeforeSendCommonObserver()
    private val _onProblemObserver: IUiGenericObserver<String> =
        _genericConfiguration.uiConfiguration.createProblemObserverForSendFileRule()

    private fun acceptFile(messageReader: DataInputStream, acceptedFilesValue: AtomicInteger) {
        acceptedFilesValue.set(acceptedFilesValue.get() + 1)

        if (messageReader.readBoolean()) {
            val fileInfo =
                _messageHandler.handleAcceptedMessage(messageReader.readUTF()).split(',')
            val fileLength = fileInfo[1].toLong()
            val path = "${_genericConfiguration.context.downloadFolderPath}/${fileInfo[0]}"
            val bufferSize =
                if (_genericConfiguration.context.dataBufferSize < fileLength) _genericConfiguration.context.dataBufferSize else fileLength.toInt()
            val buffer = ByteArray(bufferSize)
            val file = File(path)
            val fileOutputStream = FileOutputStream(file)
            var readDataSize = 0
            var fileAcceptedLength = 0L
            _logger.info(path)

            while (fileLength != fileAcceptedLength && messageReader.read(buffer)
                    .also { readDataSize = it } > 0
            ) {
                fileAcceptedLength += readDataSize.toLong()
                _logger.info(String(buffer))
                fileOutputStream.write(buffer, 0, readDataSize)
                _progressUiObserver.notifyUi(
                    FileProgressDto(
                        fileLength,
                        file.name,
                        file.absolutePath,
                        ((fileAcceptedLength / fileLength) * 100).toByte()
                    )
                )
            }
        }
    }

    private fun newConnection(
        socket: Socket,
        infoWithToken: String,
        acceptedFilesValue: AtomicInteger,
        isRunning: AtomicBoolean
    ) {
        _genericConfiguration.asyncConfiguration.runAsync {
            val messageSender = TcpIOFactory.createMessageSender(socket)
            val messageReader = TcpIOFactory.createMessagesReader(socket)
            // send token
            messageSender.writeUTF(_messageHandler.handleMessageBeforeSend(infoWithToken))
            messageSender.flush()

            if (messageReader.readBoolean()) {
                for (ignore in 0 until messageReader.readInt()) {
                    acceptFile(messageReader, acceptedFilesValue)
                }
            } else {
                _logger.error("Wrong token")
            }

            socket.close()
        }
    }

    override fun invoke(request: RequestDto<Socket>) {
        val recipientInfo =
            DeviceRequestAbstractFactory.createDeviceRequestFactory(_genericConfiguration, _logger)
                .createGetInfoRequestBuilder().doRequest(request.context.inetAddress.hostAddress)
        _onStartObserver.notifyUi(recipientInfo)
        val acceptedFiles = AtomicInteger(0)
        val messageSender = TcpIOFactory.createMessageSender(request.context)
        val messageReader = TcpIOFactory.createMessagesReader(request.context)
        val infoWithToken = _messageHandler.handleAcceptedMessage(messageReader.readUTF())
        val parsedFileInfo = infoWithToken.split(',')
        val isSingletonFile = !parsedFileInfo[2].contains(FILE_DELIMITER)
        val numberOfFiles =
            if (isSingletonFile) 0 else parsedFileInfo[2].split(FILE_DELIMITER)[0].toInt()
        val numberOfFolders =
            if (isSingletonFile) 0 else parsedFileInfo[2].split(FILE_DELIMITER)[1].toInt()
        _confirmFileMessage.ask(ConfirmFileDto(
            recipientInfo,
//                        parsedFileInfo[1].toInt(),
            numberOfFiles, numberOfFolders,
//                        isSingletonFile,
            if (isSingletonFile) parsedFileInfo[2] else null
        ), onAccept = {
            _logger.info("Accepted request")
            val isRunning = AtomicBoolean(true)
            messageSender.writeBoolean(true)
            messageSender.flush()
            _onCancelObserver.notifyUi(isRunning)
            Socket(recipientInfo.ip, _genericConfiguration.context.asyncPort).let {
                val socketMessageSender = TcpIOFactory.createMessageSender(it)
                val socketMessageReader = TcpIOFactory.createMessagesReader(it)

                // send token
                socketMessageSender.writeUTF(
                    _messageHandler.handleMessageBeforeSend(
                        infoWithToken
                    )
                )
                socketMessageSender.flush()
                // check on correct token
                if (socketMessageReader.readBoolean()) {
                    while (socketMessageReader.readBoolean()) {
                        val folderPath = socketMessageReader.readUTF()
                        File("${_genericConfiguration.context.downloadFolderPath}/${folderPath}").mkdirs()
                    }
                } else {
                    _onEnd(request)
                    return@let
                }

                socketMessageSender.writeBoolean(true)
                socketMessageSender.flush()
                it.close()
            }

            for (ignore in 0 until messageReader.readInt()) {
                Socket(recipientInfo.ip, _genericConfiguration.context.asyncPort).let {
                    this.newConnection(it, infoWithToken, acceptedFiles, isRunning)
                }
            }

            _onEnd(request)
        }, onCancel = {
            _logger.info("Canceled request")
            messageSender.writeBoolean(false)
            messageSender.flush()
            _onEnd(request)
        })
    }
}