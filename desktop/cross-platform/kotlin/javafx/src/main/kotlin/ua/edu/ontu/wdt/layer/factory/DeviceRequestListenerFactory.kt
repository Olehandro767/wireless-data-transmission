package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener
import ua.edu.ontu.wdt.layer.impl.handler.SecureRequestResponseHandle
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol.TCP
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener.TcpDeviceRequestListener
import ua.edu.ontu.wdt.layer.impl.ui.EmptyUiObserverAndMessageConfigurationImpl
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration

object DeviceRequestListenerFactory {

    fun createDeviceListener(
        context: IContext,
        asyncConfiguration: IAsyncConfiguration,
        uiObserverAndMessageConfigurationImpl: IUiObserverAndMessageConfiguration = EmptyUiObserverAndMessageConfigurationImpl(),
        logger: ILog = EmptyLogger()
    ): IDeviceRequestListener {
        val messageHandler =
            if (context.isEncryptionEnabled) SecureRequestResponseHandle() else UnsecureRequestResponseHandler()
        return when (Protocol.toEnum(context.protocol)) {
            TCP -> TcpDeviceRequestListener(
                messageHandler,
                context,
                asyncConfiguration,
                uiObserverAndMessageConfigurationImpl,
                logger = logger
            )
        }
    }
}