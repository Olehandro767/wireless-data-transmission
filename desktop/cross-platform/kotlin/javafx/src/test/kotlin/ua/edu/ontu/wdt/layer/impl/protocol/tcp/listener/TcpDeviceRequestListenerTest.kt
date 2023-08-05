package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ua.edu.ontu.wdt.configuration.StdLogger
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory.createDeviceRequestFactory
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory.createDeviceListener
import ua.edu.ontu.wdt.service.ApplicationContex.Companion.buildPropertiesReader
import ua.edu.ontu.wdt.service.ApplicationContex.Companion.readYaml
import java.io.FileInputStream

class TcpDeviceRequestListenerTest {

    @Test
    fun test() {
        val remoteIpV4 = "127.0.0.1"
        val remoteIpV6 = "0:0:0:0:0:0:0:1"
        val applicationPropertiesForTestPath = "./src/test/resources/application.yaml"
        StdLogger().let { log -> run {
            buildPropertiesReader(readYaml(FileInputStream(applicationPropertiesForTestPath))).let { env -> run {
                createDeviceListener(env, log).let { run {
                    createDeviceRequestFactory(env, it as TcpDeviceRequestListener, logger = log).let { factory -> run {
                        it.serve().let {
                            factory.createGetInfoRequestBuilder().doRequest(remoteIpV4).let { run {
                                assertEquals(remoteIpV4, it.ip)
                                assertEquals(env.deviceName, it.deviceName)
                                assertEquals(env.deviceType, it.deviceType)
                                assertEquals(env.userName, it.userName)
                            } }
                            factory.createGetInfoRequestBuilder().doRequest(remoteIpV6).let { run {
                                log.info(it.toString())
                                assertEquals(remoteIpV6, it.ip)
                            } }
                        }
                    } }
                } }
            } }
        } }
    }
}