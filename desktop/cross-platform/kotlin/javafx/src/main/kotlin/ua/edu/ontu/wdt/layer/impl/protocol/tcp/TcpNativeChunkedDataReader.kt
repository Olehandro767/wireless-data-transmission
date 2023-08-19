package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import java.io.InputStream
import java.net.Socket

class TcpNativeChunkedDataReader(
    socket: Socket,
    bufferSize: Int,
    private val ioHandler: IIOSecurityHandler,
    private val inputStream: InputStream =  socket.getInputStream()
) {

    var readDataSize = 0
    var buffer = ByteArray(bufferSize)

    fun readChunk(): TcpNativeChunkedDataReader {
        this.readDataSize += this.inputStream.read(buffer)
        this.buffer = this.ioHandler.handleAcceptedBytes(this.buffer)
        return this
    }
}