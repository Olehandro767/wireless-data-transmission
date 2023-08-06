package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.*
import ua.edu.ontu.wdt.layer.impl.handler.SecureRequestResponseHandle
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.client.TcpDeviceRequestFactory

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