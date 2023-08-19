package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import java.io.DataOutputStream
import java.io.OutputStream
import java.net.Socket

class TcpMessageSender(socket: Socket, private val ioHandler: IIOSecurityHandler) {

    private val dataOutputStream = DataOutputStream(socket.getOutputStream())

    fun prepareMessage(message: String): TcpMessageSender {
        this.dataOutputStream.writeUTF(this.ioHandler.handleMessageBeforeSend(message))
        return this
    }

    fun prepareMessage(message: Boolean): TcpMessageSender {
        this.dataOutputStream.writeBoolean(message)
        return this
    }

    fun prepareMessage(message: Int): TcpMessageSender {
        this.dataOutputStream.writeInt(message)
        return this
    }

    fun sendToRemoteDevice() = this.dataOutputStream.flush()

    fun sendMessageToRemoteDevice(message: String) = this.prepareMessage(message).sendToRemoteDevice()

    fun sendMessageToRemoteDevice(message: Boolean) = this.prepareMessage(message).sendToRemoteDevice()

    fun sendMessageToRemoteDevice(message: Int) = this.prepareMessage(message).sendToRemoteDevice()

    @Deprecated("redudant")
    fun sendChunk(chunk: ByteArray, off: Int, length: Int) {
        this.dataOutputStream.write(this.ioHandler.handleBytesBeforeSend(chunk), off, length)
        this.dataOutputStream.flush()
    }

    fun toOutputStream(): OutputStream = this.dataOutputStream
}
