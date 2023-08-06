package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.*

class TcpDeviceRequestFactory(
    private val context: IContext,
    private val messageHandler: IRequestResponseHandler,
    private val uiObserverConfiguration: IUiObserverAndMessageConfiguration,
    private val logger: ILog = EmptyLogger(),
): IDeviceRequestFactory {

    override fun createGetInfoRequestBuilder(): IGetInfoRequest = TcpGetInfoRequest(this.context, this.messageHandler,)

    override fun createSendFileRequestBuilder(): ISendFileRequestBuilder = TcpSendFileRequestBuilder(
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