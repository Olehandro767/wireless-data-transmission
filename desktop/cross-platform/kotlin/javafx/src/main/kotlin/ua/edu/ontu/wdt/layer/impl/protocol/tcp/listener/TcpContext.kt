package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import java.net.Socket
import java.util.concurrent.Semaphore

data class TcpContext(val socket: Socket, val semaphore: Semaphore)
