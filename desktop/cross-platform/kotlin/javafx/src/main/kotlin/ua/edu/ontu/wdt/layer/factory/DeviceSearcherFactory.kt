package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import ua.edu.ontu.wdt.layer.IDeviceSearcher
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory.createDeviceRequestFactory
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol.TCP
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpDeviceSearcher
import ua.edu.ontu.wdt.layer.impl.ui.EmptyUiObserverAndMessageConfigurationImpl
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration
import ua.edu.ontu.wdt.service.ApplicationContext

object DeviceSearcherFactory {

    fun createDeviceSearcher(
        applicationContext: ApplicationContext,
        asyncConfiguration: IAsyncConfiguration,
        uiObserverAndMessageConfigurationImpl: IUiObserverAndMessageConfiguration = EmptyUiObserverAndMessageConfigurationImpl(),
        logger: ILog = EmptyLogger()
    ): IDeviceSearcher {
        return when (Protocol.toEnum(applicationContext.protocol)) {
            TCP -> TcpDeviceSearcher(
                uiObserverAndMessageConfigurationImpl.createUiProgressObserver(),
                uiObserverAndMessageConfigurationImpl.createUiNewDeviceInfoObserver(),
                uiObserverAndMessageConfigurationImpl.createCancelObserver(),
                createDeviceRequestFactory(
                    applicationContext,
                    asyncConfiguration,
                    uiObserverAndMessageConfigurationImpl,
                    logger
                ),
                asyncConfiguration,
                logger
            )
        }
    }
}