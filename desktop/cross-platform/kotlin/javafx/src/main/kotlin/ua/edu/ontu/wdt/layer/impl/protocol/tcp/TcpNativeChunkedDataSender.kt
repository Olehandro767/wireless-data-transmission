package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import java.io.OutputStream
import java.net.Socket

class TcpNativeChunkedDataSender(
    socket: Socket,
    private val ioHandler: IIOSecurityHandler,
    private val outputStream: OutputStream = socket.getOutputStream()
) {

    fun sendChunkToRemoteDevice(buffer: ByteArray, transferredBytes: Int) {
        this.outputStream.write(this.ioHandler.handleAcceptedBytes(buffer), 0, transferredBytes)
        this.outputStream.flush()
    }
}