package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IDeviceRequestFactory
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.ISendFileRequestBuilder
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration

class TcpDeviceRequestFactory(
    private val context: IContext,
    private val messageHandler: IIOSecurityHandler,
    private val uiObserverConfiguration: IUiObserverAndMessageConfiguration,
    private val logger: ILog = EmptyLogger(),
) : IDeviceRequestFactory {

    override fun createGetInfoRequestBuilder(): IGetInfoRequest = TcpGetInfoRequest(this.context, this.messageHandler)

    override fun createSendFileRequestBuilder(): ISendFileRequestBuilder = if (this.context.maxThreadsForSending <= 1)
        TcpLegacySendFileRequestBuilder(
            this.logger,
            this.context,
            this.messageHandler,
            this.createGetInfoRequestBuilder(),
            this.uiObserverConfiguration.createBeforeSendCommonObserver(),
            this.uiObserverConfiguration.createProgressObserverForSendFileRule(),
            this.uiObserverConfiguration.createFinishObserverForSendFileRule(),
            this.uiObserverConfiguration.createCancelObserverForSendFileRule(),
            this.uiObserverConfiguration.createProblemObserverForSendFileRule()
        )
    else TcpMultiSendFileRequestBuilder(
        this.logger,
        this.context,
        this.messageHandler,
        this.createGetInfoRequestBuilder(),
        this.uiObserverConfiguration.createBeforeSendCommonObserver(),
        this.uiObserverConfiguration.createProgressObserverForSendFileRule(),
        this.uiObserverConfiguration.createFinishObserverForSendFileRule(),
        this.uiObserverConfiguration.createCancelObserverForSendFileRule(),
        this.uiObserverConfiguration.createProblemObserverForSendFileRule(),
    )
}