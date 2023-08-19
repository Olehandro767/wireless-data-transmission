package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.client.RequestDto
import java.net.Socket

class TcpGetFileSystem(
    private val onEnd: ITcpLambda
): ITcpLambda {

    override fun invoke(request: RequestDto<Socket>) {
        TODO("Not yet implemented")
    }
}