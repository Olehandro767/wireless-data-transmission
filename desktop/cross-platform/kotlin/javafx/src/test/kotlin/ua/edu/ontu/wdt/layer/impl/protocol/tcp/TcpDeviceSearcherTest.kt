package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import org.junit.jupiter.api.Test
import ua.edu.ontu.wdt.configuration.AsyncConfiguration
import ua.edu.ontu.wdt.configuration.StdLogger
import ua.edu.ontu.wdt.configuration.StdUiObserverConfiguration
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory
import ua.edu.ontu.wdt.layer.factory.DeviceSearcherFactory
import ua.edu.ontu.wdt.service.ApplicationContext
import java.io.FileInputStream
import java.util.concurrent.CountDownLatch

class TcpDeviceSearcherTest {

    @Test
    fun test() {
        val latch = CountDownLatch(255)
        val logger = StdLogger()
        val applicationPropertiesForTestPath = "./src/test/resources/application.yaml"
        val uiConf = StdUiObserverConfiguration(logger, latch)
        val context =
            ApplicationContext.buildContext(ApplicationContext.readYaml(FileInputStream(applicationPropertiesForTestPath)))
        DeviceRequestListenerFactory.createDeviceListener(context, AsyncConfiguration(), uiConf, logger).serve()
        DeviceSearcherFactory.createDeviceSearcher(context, AsyncConfiguration(), uiConf, logger).search()
        latch.await()
    }
}