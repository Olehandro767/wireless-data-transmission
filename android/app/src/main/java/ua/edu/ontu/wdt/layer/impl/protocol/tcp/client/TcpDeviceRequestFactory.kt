package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IDeviceRequestFactory
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.ISendFileRequestBuilder
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger

class TcpDeviceRequestFactory(
    private val genericConfiguration: WdtGenericConfiguration<*, *>,
    private val messageHandler: IIOSecurityHandler,
    private val logger: ILog = EmptyLogger(),
) : IDeviceRequestFactory {

    override fun createGetInfoRequestBuilder(): IGetInfoRequest =
        TcpGetInfoRequest(this.genericConfiguration.context, this.messageHandler)

    override fun createSendFileRequestBuilder(): ISendFileRequestBuilder =
        if (this.genericConfiguration.context.maxThreadsForSending <= 1) TcpLegacySendFileRequestBuilder(
            this.logger,
            this.genericConfiguration.context,
            this.messageHandler,
            this.createGetInfoRequestBuilder(),
            this.genericConfiguration.uiConfiguration.createBeforeSendCommonObserver(),
            this.genericConfiguration.uiConfiguration.createProgressObserverForSendFileRule(),
            this.genericConfiguration.uiConfiguration.createFinishObserverForSendFileRule(),
            this.genericConfiguration.uiConfiguration.createCancelObserverForSendFileRule(),
            this.genericConfiguration.uiConfiguration.createProblemObserverForSendFileRule()
        )
        else TcpMultiSendFileRequestBuilder(
            this.logger,
            this.genericConfiguration.context,
            this.genericConfiguration.asyncConfiguration,
            this.messageHandler,
            this.createGetInfoRequestBuilder(),
            this.genericConfiguration.uiConfiguration.createBeforeSendCommonObserver(),
            this.genericConfiguration.uiConfiguration.createProgressObserverForSendFileRule(),
            this.genericConfiguration.uiConfiguration.createFinishObserverForSendFileRule(),
            this.genericConfiguration.uiConfiguration.createCancelObserverForSendFileRule(),
            this.genericConfiguration.uiConfiguration.createProblemObserverForSendFileRule(),
        )
}