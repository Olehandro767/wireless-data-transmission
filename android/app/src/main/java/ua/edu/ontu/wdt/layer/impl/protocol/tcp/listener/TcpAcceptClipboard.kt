package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.IClipboardConfiguration.Companion.STRING_DATA
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.RequestDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageReader
import java.net.Socket

class TcpAcceptClipboard(
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
    private val _messageHandler: IIOSecurityHandler,
    private val onEnd: ITcpLambda
) : ITcpLambda {

    override fun invoke(request: RequestDto<Socket>) {
        val messageReader = TcpMessageReader(request.context, _messageHandler)
        // reading type of data
        when (messageReader.readMessageFromRemoteDevice()) {
            STRING_DATA -> _genericConfiguration.clipboardConfiguration.writeText(messageReader.readMessageFromRemoteDevice())
        }
        this.onEnd(request)
    }
}