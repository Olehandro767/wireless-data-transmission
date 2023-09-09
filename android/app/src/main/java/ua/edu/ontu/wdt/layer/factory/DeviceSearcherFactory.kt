package ua.edu.ontu.wdt.layer.factory

import ua.edu.ontu.wdt.layer.IDeviceSearcher
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory.createDeviceRequestFactory
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol.TCP
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpDeviceSearcher

object DeviceSearcherFactory {

    fun createDeviceSearcher(
        genericConfiguration: WdtGenericConfiguration<*, *>, logger: ILog = EmptyLogger()
    ): IDeviceSearcher {
        return when (genericConfiguration.context.protocol) {
            TCP -> TcpDeviceSearcher(
                createDeviceRequestFactory(
                    genericConfiguration, logger
                ), genericConfiguration, logger
            )
        }
    }
}