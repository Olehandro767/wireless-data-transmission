package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.*
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener.TcpDeviceRequestListener

class TcpDeviceRequestFactory(
    private val context: IContext,
    private val messageHandler: IRequestResponseHandler,
    private val tcpDeviceRequestListener: TcpDeviceRequestListener,
    private val uiObserverConfiguration: IUiObserverConfiguration? = null,
    private val logger: ILog = EmptyLogger(),
): IDeviceRequestFactory {

    override fun createGetInfoRequestBuilder(): IGetInfoRequest = TcpGetInfoRequest(this.context, this.messageHandler,)

    override fun createSendFileRequestBuilder(): ISendFileRequestBuilder = if (uiObserverConfiguration == null)
        TcpSendFileRequestBuilder(
            this.logger,
            this.context,
            this.messageHandler,
            this.createGetInfoRequestBuilder(),
            this.tcpDeviceRequestListener
        )
    else
        TcpSendFileRequestBuilder(
            this.logger,
            this.context,
            this.messageHandler,
            this.createGetInfoRequestBuilder(),
            this.tcpDeviceRequestListener,
            this.uiObserverConfiguration.createBeforeSendCommonObserver(),
            this.uiObserverConfiguration.createProgressObserverForSendFileRule(),
            this.uiObserverConfiguration.createFinishObserverForSendFileRule(),
            this.uiObserverConfiguration.createProblemObserverForSendFileRule(),
        )
}