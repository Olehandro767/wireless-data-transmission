package ua.edu.ontu.wdt.layer.dto.file

import ua.edu.ontu.wdt.layer.dto.GetInfoDto

data class ConfirmFileDto(
    val deviceInfoDto: GetInfoDto,
    val files: Int,
    val folders: Int,
    val singletonFileName: String?,
    val numberOfAllFiles: Int = files + folders,
)