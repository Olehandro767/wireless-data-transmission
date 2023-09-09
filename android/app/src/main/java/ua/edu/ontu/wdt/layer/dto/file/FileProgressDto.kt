package ua.edu.ontu.wdt.layer.dto.file

data class FileProgressDto(
    val size: Long,
    val name: String,
    val path: String,
    val progress: Byte, // from 0 to 100
)
