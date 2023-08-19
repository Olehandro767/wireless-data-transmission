package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.RequestDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpIOFactory
import java.net.Socket

class TcpGetInfo(
        private val messageHandler: IIOSecurityHandler,
        private val context: IContext,
        private val onEnd: ITcpLambda,
): ITcpLambda {

    override fun invoke(request: RequestDto<Socket>) {
        val messageSender = TcpIOFactory.createMessageSender(request.context)
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(
            "${this.context.deviceName},${this.context.userName},${this.context.deviceType},${this.context.maxThreadsForSending}"
        ))
        messageSender.flush()
        this.onEnd(request)
    }
}