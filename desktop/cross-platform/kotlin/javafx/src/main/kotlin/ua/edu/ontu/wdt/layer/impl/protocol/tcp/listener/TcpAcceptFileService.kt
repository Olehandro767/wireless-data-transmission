package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import kotlinx.coroutines.*
import ua.edu.ontu.wdt.layer.*
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.ConfirmFileDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpUtils
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TcpAcceptFileService(
    private val logger: ILog,
    private val context: IContext,
    private val onEnd: ITcpLambda,
    private val messageHandler: IRequestResponseHandler,
    private val confirmFileMessage: IUiGenericConfirmMessage<ConfirmFileDto>,
    private val progressUiObserver: IUiGenericObserver<FileProgressDto>,
    private val onCancelObserver: IUiGenericObserver<AtomicBoolean>,
    private val onStartObserver: IUiGenericObserver<GetInfoDto>,
    private val onProblemObserver: IUiGenericObserver<String>,
): ITcpLambda {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        const val FILE_DELIMITER = "{./*}"
    }

    private fun acceptFile(messageReader: DataInputStream, acceptedFilesValue: AtomicInteger) {
        acceptedFilesValue.set(acceptedFilesValue.get() + 1)

        if (messageReader.readBoolean()) {
            val fileInfo = this.messageHandler.handleAcceptedMessage(messageReader.readUTF()).split(',')
            val fileLength = fileInfo[1].toLong()
            val path = "${this.context.downloadFolderPath}/${fileInfo[0]}"
            val buffer = ByteArray(this.context.dataBufferSize)
            val file = File(path)
            val fileOutputStream = FileOutputStream(file)
            var readDataSize: Int
            var fileAcceptedLength = 0
            this.logger.info(path)

            while (messageReader.read(buffer).also { readDataSize = it } > 0) {
                fileAcceptedLength += readDataSize
                fileOutputStream.write(buffer, 0, readDataSize)
                this.progressUiObserver.notifyUi(FileProgressDto(
                    fileLength,
                    file.name,
                    file.absolutePath,
                    ((fileAcceptedLength / fileLength) * 100).toByte()
                ))
            }
        }
    }

    private suspend fun acceptFolder(messageReader: DataInputStream, acceptedFilesValue: AtomicInteger) {
        withContext(this.ioDispatcher) {
            messageHandler.handleAcceptedMessage(messageReader.readUTF()).let {
                if (it != "{}/*IGNORE_THIS_FOLDER/*{}") {
                    File("${context.downloadFolderPath}/$it").mkdirs()

                    if (messageReader.readBoolean()) {
                        acceptFolder(messageReader, acceptedFilesValue)
                    } else {
                        acceptFile(messageReader, acceptedFilesValue)
                    }
                }
            }
        }
    }

    private suspend fun newConnection(
        ip: String,
        infoWithToken: String,
        acceptedFilesValue: AtomicInteger,
        isRunning: AtomicBoolean
    ) {
        withContext(this.ioDispatcher) {
            Socket(ip, context.asyncPort).let {
                val messageReader = TcpUtils.createMessagesReader(it)
                val messageSender = TcpUtils.createMessageSender(it)

                if (messageReader.readBoolean()) {
                    logger.info("Accepted new Connection")
                    messageSender.writeBoolean(isRunning.get())
                    messageSender.writeUTF(messageHandler.handleMessageBeforeSend(infoWithToken))
                    messageSender.flush()

                    if (messageReader.readBoolean()) {
                        when (messageHandler.handleAcceptedMessage(messageReader.readUTF())) {
                            "file" -> acceptFile(messageReader, acceptedFilesValue)
                            "folder" -> acceptFolder(messageReader, acceptedFilesValue)
                        }
                    } else {
                        onProblemObserver.notifyUi("invalid_token")
                        it.close()
                    }
                } else {
                    onProblemObserver.notifyUi("canceled_file_sending")
                    it.close()
                }
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun invoke(request: RequestDto<Socket>) {
        val recipientInfo = DeviceRequestAbstractFactory.createDeviceRequestFactory(this.context)
            .createGetInfoRequestBuilder().doRequest(request.context.inetAddress.hostAddress)
        this.onStartObserver.notifyUi(recipientInfo)
        val acceptedFiles = AtomicInteger(0)
        val messageSender = TcpUtils.createMessageSender(request.context)
        val messageReader = TcpUtils.createMessagesReader(request.context)
        val infoWithToken = this.messageHandler.handleAcceptedMessage(messageReader.readUTF())
        val parsedFileInfo = infoWithToken.split(',')
        val isSingletonFile = !parsedFileInfo[2].contains(FILE_DELIMITER)
        val numberOfFiles = if (isSingletonFile) 0 else parsedFileInfo[2].split(FILE_DELIMITER)[0].toInt()
        val numberOfFolders = if (isSingletonFile) 0 else parsedFileInfo[2].split(FILE_DELIMITER)[1].toInt()
        this.confirmFileMessage.ask(
            ConfirmFileDto(
                recipientInfo,
                parsedFileInfo[1].toInt(),
                numberOfFiles,
                numberOfFolders,
                isSingletonFile,
                if (isSingletonFile) parsedFileInfo[2] else null
            ),
            onAccept = {
                logger.info("Accepted request")
                val isRunning = AtomicBoolean(true)
                messageSender.writeBoolean(true)
                messageSender.flush()
                this.onCancelObserver.notifyUi(isRunning)

                for (ignore in 1..parsedFileInfo[0].toInt()) {
                    GlobalScope.launch { newConnection(recipientInfo.ip, infoWithToken, acceptedFiles, isRunning) }
                }

                this.onEnd(request)
            },
            onCancel = {
                logger.info("Canceled request")
                messageSender.writeBoolean(false)
                messageSender.flush()
                this.onEnd(request)
            }
        )
    }
}