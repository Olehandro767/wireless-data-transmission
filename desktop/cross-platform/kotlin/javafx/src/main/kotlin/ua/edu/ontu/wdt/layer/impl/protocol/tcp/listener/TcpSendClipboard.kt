package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.RequestDto
import java.net.Socket

class TcpSendClipboard(
    private val onEnd: ITcpLambda
): ITcpLambda {

    override fun invoke(request: RequestDto<Socket>) {
        TODO("Not yet implemented")
    }
}