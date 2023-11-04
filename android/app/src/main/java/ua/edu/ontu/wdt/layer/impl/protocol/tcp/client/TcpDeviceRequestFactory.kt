package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IDeviceRequestFactory
import ua.edu.ontu.wdt.layer.client.IGetClipboardRequestBuilder
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.ISendClipboardRequestBuilder
import ua.edu.ontu.wdt.layer.client.ISendFileRequestBuilder
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger

class TcpDeviceRequestFactory(
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
    private val _messageHandler: IIOSecurityHandler,
    private val _logger: ILog = EmptyLogger(),
) : IDeviceRequestFactory {

    override fun createGetInfoRequestBuilder(): IGetInfoRequest =
        TcpGetInfoRequest(_genericConfiguration.context, _messageHandler)

    override fun createSendFileRequestBuilder(): ISendFileRequestBuilder =
        if (_genericConfiguration.context.maxThreadsForSending <= 1) TcpLegacySendFileRequestBuilder(
            _logger,
            _genericConfiguration.context,
            _messageHandler,
            this.createGetInfoRequestBuilder(),
            _genericConfiguration.uiConfiguration.createBeforeSendCommonObserver(),
            _genericConfiguration.uiConfiguration.createProgressObserverForSendFileRule(),
            _genericConfiguration.uiConfiguration.createFinishObserverForSendFileRule(),
            _genericConfiguration.uiConfiguration.createCancelObserverForSendFileRule(),
            _genericConfiguration.uiConfiguration.createProblemObserverForSendFileRule()
        )
        else TcpMultiSendFileRequestBuilder(
            _logger,
            _genericConfiguration.context,
            _genericConfiguration.asyncConfiguration,
            _messageHandler,
            this.createGetInfoRequestBuilder(),
            _genericConfiguration.uiConfiguration.createBeforeSendCommonObserver(),
            _genericConfiguration.uiConfiguration.createProgressObserverForSendFileRule(),
            _genericConfiguration.uiConfiguration.createFinishObserverForSendFileRule(),
            _genericConfiguration.uiConfiguration.createCancelObserverForSendFileRule(),
            _genericConfiguration.uiConfiguration.createProblemObserverForSendFileRule(),
        )

    override fun getClipboardRequestBuilder(): IGetClipboardRequestBuilder {
        TODO("Not yet implemented")
    }

    override fun createSendClipboardRequestBuilder(): ISendClipboardRequestBuilder =
        TcpSendClipboardRequestBuilder(_messageHandler, _genericConfiguration)
}