package ua.edu.ontu.wdt.service

import org.yaml.snakeyaml.Yaml
import ua.edu.ontu.wdt.layer.DeviceType
import ua.edu.ontu.wdt.layer.DeviceType.PC
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.utils.Ipv4Utils.getIpsV4ForCurrentDevice
import java.io.File
import java.io.InputStream
import java.lang.Runtime.getRuntime
import java.lang.System.getProperty
import java.net.InetAddress.getLocalHost

@Suppress("ArrayInDataClass")
data class ApplicationContext(
    override var port: Int,
    override var asyncPort: Int = 0,
    override var protocol: String,
    override var appFolderName: String,
    override var appFolderPath: String,
    override var downloadFolderPath: String = "${getProperty("user.home")}/Download",
    override var dataBufferSize: Int = 12582912,
    override var numberOfListeners: Int = getRuntime().availableProcessors(),
    override var ipv4: Array<String> = getIpsV4ForCurrentDevice(),
    override var ipv6: Array<String>? = null,
    override var deviceName: String = getLocalHost().hostName,
    override var userName: String = getProperty("user.name"),
    override var isEncryptionEnabled: Boolean = false,
    override var maxNumberOfConnections: Int = 15000,
    override var deviceType: DeviceType = PC,
    override var maxThreadsForSending: Int = 0,
    var localizationFolder: String,
) : IContext {

    companion object {

        private fun createOsBasedApplicationFolderPath(): String =
            if (getProperty("os.name").contains("Linux", true)) "${getProperty("user.home")}/.wdt"
            else "${getProperty("user.home")}/WDT"

        fun readYaml(inputStream: InputStream): HashMap<String, Any> = Yaml().let {
            return it.loadAs(inputStream, HashMap::class.java)
        }

        fun createApplicationYamlPath(): String {
            val fileName = "application.yaml"

            return if (File("./$fileName").exists()) {
                "./$fileName"
            } else {
                "${createOsBasedApplicationFolderPath()}/$fileName"
            }
        }

        fun buildContext(
            yamlFileData: Map<String, Any>
        ): ApplicationContext {
            val osProperty = yamlFileData["os"] as Map<*, *>
            val folderNames = osProperty["folder-name"] as Map<*, *>
            val appFolderName = createOsBasedApplicationFolderPath()
            val defaultProperties = yamlFileData["default-properties"] as Map<*, *>
            val appFolderPath = "${getProperty("user.home")}/${appFolderName}"
            val port = defaultProperties["port"] as Int
            val protocol = defaultProperties["protocol"] as String
            val isEncryptionEnabled = defaultProperties["encryption"] as Boolean
            val localizationFolderName = folderNames["localization"] as String
            val localizationFolderPath = if (File("./$localizationFolderName").exists()) "./$localizationFolderName"
            else "$appFolderName/$localizationFolderName"
            return ApplicationContext(
                port,
                0,
                protocol,
                appFolderName,
                appFolderPath,
                isEncryptionEnabled = isEncryptionEnabled,
                localizationFolder = localizationFolderPath
            )
        }
    }
}