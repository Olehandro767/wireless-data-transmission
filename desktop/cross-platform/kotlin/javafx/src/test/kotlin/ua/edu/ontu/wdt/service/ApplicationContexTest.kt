package ua.edu.ontu.wdt.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import ua.edu.ontu.wdt.configuration.StdLogger
import ua.edu.ontu.wdt.service.ApplicationContex.Companion.buildPropertiesReader
import ua.edu.ontu.wdt.service.ApplicationContex.Companion.readYaml
import java.io.FileInputStream

class ApplicationContexTest {

    @Test
    fun test() {
        StdLogger().let { log -> run {
            buildPropertiesReader(readYaml(FileInputStream("./src/test/resources/application.yaml"))).let { run {
                assertEquals(4000, it.port)
                assertEquals("tcp", it.protocol)
                assertFalse(it.isEncryptionEnabled)
                log.debug("app folder: ${it.appFolderPath}")
            } }
        } }
    }
}