package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.EmptyLogger
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.IDeviceRequestListener
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.impl.handler.SecureRequestResponseHandle
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener.TcpDeviceRequestListener

object DeviceRequestListenerFactory {

    fun createDeviceListener(context: IContext, logger: ILog = EmptyLogger()): IDeviceRequestListener {
        val messageHandler = if (context.isEncryptionEnabled) SecureRequestResponseHandle() else UnsecureRequestResponseHandler()
        return when(context.protocol) {
            "tcp" -> TcpDeviceRequestListener(messageHandler, context, logger = logger)
            else -> throw IllegalArgumentException()
        }
    }
}