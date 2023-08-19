package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.IRequest
import ua.edu.ontu.wdt.layer.client.ISendFileRequestBuilder
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.dto.file.FileProgressDto
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.ui.IUiObserver
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class TcpLegacySendFileRequestBuilder(
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

    lateinit var ip: String
    lateinit var files: Array<out File>

    override fun ip(ip: String): ISendFileRequestBuilder {
        this.ip = ip
        return this
    }

    override fun files(vararg files: File): ISendFileRequestBuilder {
        this.files = files
        return this
    }

    override fun doRequest(ip: String, vararg files: File) {
        TODO("Not yet implemented")
    }

    override fun build(): IRequest = TcpLegacySendFileRequest(
            this.ip,
            this.logger,
            this.context,
            this.messageHandler,
            this.getInfoConfiguration,
            this.onStartObserver,
            this.progressUiObserver,
            this.onFinishObserver,
            this.onCancelObserver,
            this.onProblemObserver,
            this.files
    )
}