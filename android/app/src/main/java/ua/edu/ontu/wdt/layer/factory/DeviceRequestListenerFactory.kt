package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener
import ua.edu.ontu.wdt.layer.impl.handler.SecureRequestResponseHandle
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol.TCP
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener.TcpDeviceRequestListener

object DeviceRequestListenerFactory {

    fun createDeviceListener(
        genericConfiguration: WdtGenericConfiguration<*, *>, logger: ILog = EmptyLogger()
    ): IDeviceRequestListener {
        val messageHandler =
            if (genericConfiguration.context.isEncryptionEnabled) SecureRequestResponseHandle() else UnsecureRequestResponseHandler()
        return when (genericConfiguration.context.protocol) {
            TCP -> TcpDeviceRequestListener(
                messageHandler, genericConfiguration, _logger = logger
            )
        }
    }
}