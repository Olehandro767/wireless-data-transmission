package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.IRequestResponseHandler
import ua.edu.ontu.wdt.layer.RequestDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpUtils
import java.net.Socket

class TcpGetInfo(
    private val messageHandler: IRequestResponseHandler,
    private val context: IContext,
    private val onEnd: ITcpLambda,
): ITcpLambda {

    override fun invoke(request: RequestDto<Socket>) {
        val messageSender = TcpUtils.createMessageSender(request.context)
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(
            "${this.context.deviceName},${this.context.userName},${this.context.deviceType}"
        ))
        messageSender.flush()
        this.onEnd(request)
    }
}