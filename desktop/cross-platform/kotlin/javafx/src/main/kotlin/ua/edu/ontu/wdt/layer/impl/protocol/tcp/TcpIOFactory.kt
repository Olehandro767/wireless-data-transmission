package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

@Deprecated("Use native TcpMessageReader / Sender constructor")
object TcpIOFactory {

    @Deprecated("use another createMessagesReader", ReplaceWith("DataInputStream(BufferedInputStream(socket.getInputStream()))", "java.io.DataInputStream", "java.io.BufferedInputStream"))
    fun createMessagesReader(socket: Socket): DataInputStream
            = DataInputStream(BufferedInputStream(socket.getInputStream()))

    @Deprecated("use another createMessageSender", ReplaceWith("DataOutputStream(socket.getOutputStream())", "java.io.DataOutputStream"))
    fun createMessageSender(socket: Socket): DataOutputStream
            = DataOutputStream(socket.getOutputStream())

    fun createMessagesReader(socket: Socket, ioHandler: IIOSecurityHandler): TcpMessageReader
            = TcpMessageReader(socket, ioHandler)

    fun createMessageSender(socket: Socket, ioHandler: IIOSecurityHandler): TcpMessageSender
            = TcpMessageSender(socket, ioHandler)
}