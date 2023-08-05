package ua.edu.ontu.wdt.service

import org.yaml.snakeyaml.Yaml
import ua.edu.ontu.wdt.layer.DeviceType
import ua.edu.ontu.wdt.layer.DeviceType.PC
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.utils.system.Ipv4Utils.getIpsV4ForCurrentDevice
import java.io.InputStream
import java.lang.Runtime.getRuntime
import java.lang.System.getProperty
import java.net.InetAddress.getLocalHost

class ApplicationContex(
    override var port: Int,
    override var asyncPort: Int,
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
): IContext {

    companion object {
        fun readYaml(inputStream: InputStream): HashMap<String, Any> = Yaml().let {
            return it.loadAs(inputStream, HashMap::class.java)
        }

        fun buildPropertiesReader(
            yamlFileData: Map<String, Any>
        ): ApplicationContex {
            val os = getProperty("os.name")
            val appFolderNames = ((yamlFileData["os"] as Map<*, *>)["application-folder"] as Map<*, *>)
            val appFolderName = if (os.contains("Linux", true)) {
                appFolderNames["linux"] as String
            } else {
                appFolderNames["windows"] as String
            }
            val defaultProperties = yamlFileData["default-properties"] as Map<*, *>
            val appFolderPath = "${getProperty("user.home")}/${appFolderName}"
            val port = defaultProperties["port"] as Int
            val asyncPort = defaultProperties["async-port"] as Int
            val protocol = defaultProperties["protocol"] as String
            val isEncryptionEnabled = defaultProperties["encryption"] as Boolean
            return ApplicationContex(port, asyncPort, protocol, appFolderName, appFolderPath, isEncryptionEnabled = isEncryptionEnabled)
        }
    }
}