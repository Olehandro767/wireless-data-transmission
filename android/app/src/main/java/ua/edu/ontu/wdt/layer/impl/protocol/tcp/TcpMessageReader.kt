package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.InputStream
import java.net.Socket

class TcpMessageReader(socket: Socket, private val ioHandler: IIOSecurityHandler) {

    private val dataInputStream = DataInputStream(BufferedInputStream(socket.getInputStream()))

    fun readMessageFromRemoteDevice(): String =
        this.ioHandler.handleAcceptedMessage(this.dataInputStream.readUTF())

    fun readBoolMessageFromRemoteDevice(): Boolean = this.dataInputStream.readBoolean()

    fun readIntNumberMessageFromRemoteDevice(): Int = this.dataInputStream.readInt()

    fun toInputStream(): InputStream = this.dataInputStream
}
