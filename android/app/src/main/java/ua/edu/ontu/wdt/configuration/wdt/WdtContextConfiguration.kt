package ua.edu.ontu.wdt.configuration.wdt

import ua.edu.ontu.wdt.layer.DeviceType
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.impl.protocol.Protocol
import ua.edu.ontu.wdt.layer.utils.Ipv4Utils.getIpsV4ForCurrentDevice
import java.lang.Runtime.getRuntime

data class WdtContextConfiguration(
    override var port: Int = 4000,
    override var asyncPort: Int = 0,
    override var protocol: Protocol = Protocol.TCP,
    override var userName: String = "root",
    override var deviceName: String = "android",
    override var dataBufferSize: Int = 12582912,
    override var appFolderName: String = "",
    override var appFolderPath: String = "",
    override var downloadFolderPath: String = "",
    override var ipv4: Array<String> = getIpsV4ForCurrentDevice(),
    override var ipv6: Array<String>? = null,
    override var numberOfListeners: Int = 0,
    override var deviceType: DeviceType = DeviceType.MOBILE,
    override var isEncryptionEnabled: Boolean = false,
    override var maxNumberOfConnections: Int = 15000,
    override var maxThreadsForSending: Int = 0,
    override var maxThreadsForSearching: Int = getRuntime().availableProcessors() * 5
) : IContext {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WdtContextConfiguration

        if (deviceType != other.deviceType) return false

        return true
    }

    override fun hashCode(): Int {
        return deviceType.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }
}