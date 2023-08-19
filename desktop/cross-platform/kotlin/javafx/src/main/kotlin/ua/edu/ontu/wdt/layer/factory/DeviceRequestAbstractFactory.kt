package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IDeviceRequestFactory
import ua.edu.ontu.wdt.layer.impl.handler.SecureRequestResponseHandle
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.client.TcpDeviceRequestFactory
import ua.edu.ontu.wdt.layer.impl.ui.EmptyUiObserverAndMessageConfigurationImpl
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration

object DeviceRequestAbstractFactory {

    fun createDeviceRequestFactory(
            context: IContext,
            uiObserverConfiguration: IUiObserverAndMessageConfiguration = EmptyUiObserverAndMessageConfigurationImpl(),
            logger: ILog = EmptyLogger(),
    ): IDeviceRequestFactory {
        val messageHandler = if (context.isEncryptionEnabled) SecureRequestResponseHandle() else UnsecureRequestResponseHandler()
        return when (context.protocol) {
            "tcp" -> TcpDeviceRequestFactory(
                context,
                messageHandler,
                uiObserverConfiguration,
                logger
            )
            else -> throw IllegalArgumentException()
        }
    }
}