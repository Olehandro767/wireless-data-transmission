package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.RequestDto
import java.net.Socket

fun interface ITcpLambda: (RequestDto<Socket>) -> Unit