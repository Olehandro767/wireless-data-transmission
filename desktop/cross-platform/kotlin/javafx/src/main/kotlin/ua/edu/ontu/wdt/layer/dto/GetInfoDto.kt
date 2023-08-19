package ua.edu.ontu.wdt.layer.dto

import ua.edu.ontu.wdt.layer.DeviceType

data class GetInfoDto(
    val ip: String,
    val deviceName: String,
    val userName: String,
    val deviceType: DeviceType,
    val maxThreadsForSending: Int
)
