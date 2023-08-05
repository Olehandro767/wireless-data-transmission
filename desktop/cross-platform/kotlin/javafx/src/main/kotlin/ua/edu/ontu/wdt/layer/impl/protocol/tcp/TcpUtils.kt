package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

object TcpUtils {

    fun createMessagesReader(socket: Socket): DataInputStream
            = DataInputStream(BufferedInputStream(socket.getInputStream()))

    fun createMessageSender(socket: Socket): DataOutputStream
            = DataOutputStream(socket.getOutputStream())
}