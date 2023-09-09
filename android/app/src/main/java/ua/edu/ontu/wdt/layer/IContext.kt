package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.impl.protocol.Protocol

interface IContext {

    var port: Int

    @Deprecated("Use Array")
    var asyncPort: Int
    var protocol: Protocol
    var userName: String
    var deviceName: String
    var dataBufferSize: Int
    var appFolderName: String
    var appFolderPath: String
    var downloadFolderPath: String
    var ipv4: Array<String>
    var ipv6: Array<String>?
    var numberOfListeners: Int
    var deviceType: DeviceType
    var isEncryptionEnabled: Boolean
    var maxNumberOfConnections: Int
    var maxThreadsForSending: Int
    var maxThreadsForSearching: Int
}
