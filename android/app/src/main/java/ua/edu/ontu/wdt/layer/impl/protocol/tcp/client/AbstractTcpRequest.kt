package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.IRequest
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageReader
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageSender
import ua.edu.ontu.wdt.layer.utils.RequestUtils.prepareRequestType
import java.net.Socket

abstract class AbstractTcpRequest(
    private val _ip: String,
    private val _ioHandler: IIOSecurityHandler,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
) : IRequest {

    protected abstract val requestType: String
    protected lateinit var messageReader: TcpMessageReader
    protected lateinit var messageSender: TcpMessageSender

    protected abstract fun doTcpRequest()

    override fun doRequest() {
        Socket(_ip, _genericConfiguration.context.port).let {
            this.messageReader = TcpMessageReader(it, _ioHandler)
            this.messageSender = TcpMessageSender(it, _ioHandler)
            this.messageSender.sendMessageToRemoteDevice(prepareRequestType(this.requestType))
            this.doTcpRequest()
        }
    }
}