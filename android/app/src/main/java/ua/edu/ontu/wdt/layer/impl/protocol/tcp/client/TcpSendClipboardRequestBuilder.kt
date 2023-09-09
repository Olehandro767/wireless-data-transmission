package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.IRequest
import ua.edu.ontu.wdt.layer.client.ISendClipboardRequestBuilder

class TcpSendClipboardRequestBuilder(
    private val _ioHandler: IIOSecurityHandler,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>
) : ISendClipboardRequestBuilder {

    private lateinit var _ip: String

    override fun ip(ip: String): ISendClipboardRequestBuilder {
        _ip = ip
        return this
    }

    override fun build(): IRequest = TcpSendClipboardRequest(_ip, _ioHandler, _genericConfiguration)
}