package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ua.edu.ontu.wdt.configuration.StdLogger
import ua.edu.ontu.wdt.configuration.StdUiObserverConfiguration
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory.createDeviceRequestFactory
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory.createDeviceListener
import ua.edu.ontu.wdt.service.ApplicationContex.Companion.buildPropertiesReader
import ua.edu.ontu.wdt.service.ApplicationContex.Companion.readYaml
import ua.edu.ontu.wdt.tools.FileUtils
import java.io.File
import java.io.FileInputStream

class TcpDeviceRequestListenerTest {

    @Test
    fun test() {
        val remoteIpV4 = "127.0.0.1"
        val remoteIpV6 = "0:0:0:0:0:0:0:1"
        val applicationPropertiesForTestPath = "./src/test/resources/application.yaml"
        FileUtils.createTestFiles()
        StdLogger().let { log -> run {
            buildPropertiesReader(readYaml(FileInputStream(applicationPropertiesForTestPath))).also {
                it.downloadFolderPath = "./test_folder/dir2"
            }.let { context -> run {
                StdUiObserverConfiguration(log).let { uiConfig -> run {
                    createDeviceListener(context, uiConfig, log).let { run {
                        createDeviceRequestFactory(context, uiConfig, logger = log).let { factory -> run {
                            it.serve().let {
                                factory.createGetInfoRequestBuilder().doRequest(remoteIpV4).let { run {
                                    assertEquals(remoteIpV4, it.ip)
                                    assertEquals(context.deviceName, it.deviceName)
                                    assertEquals(context.deviceType, it.deviceType)
                                    assertEquals(context.userName, it.userName)
                                } }
                                factory.createGetInfoRequestBuilder().doRequest(remoteIpV6).let { run {
                                    log.info(it.toString())
                                    assertEquals(remoteIpV6, it.ip)
                                } }

                                val files = arrayListOf(
                                    File("./test_folder/dir1"),
                                    File("./test_folder/file1.txt")
                                )
                                val wdtZipFile = File("./target/WDT.zip")

                                if (wdtZipFile.exists()) {
                                    files.add(wdtZipFile)
                                }

                                factory.createSendFileRequestBuilder().doRequest(remoteIpV4, *files.toTypedArray())
                            }
                        } }
                    } }
                } }
            } }
        } }
    }
}