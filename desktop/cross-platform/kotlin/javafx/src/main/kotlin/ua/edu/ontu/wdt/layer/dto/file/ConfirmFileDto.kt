package ua.edu.ontu.wdt.layer.dto.file

import ua.edu.ontu.wdt.layer.dto.GetInfoDto

data class ConfirmFileDto(
    val infoDto: GetInfoDto,
    val numberOfAllFiles: Int,
    val files: Int,
    val folders: Int,
    val isSingletonFile: Boolean,
    val singletonFileName: String?,
)