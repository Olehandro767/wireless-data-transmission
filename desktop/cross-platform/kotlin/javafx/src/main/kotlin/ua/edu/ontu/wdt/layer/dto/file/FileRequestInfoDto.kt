package ua.edu.ontu.wdt.layer.dto.file

data class FileRequestInfoDto(
        val rawString: String,
        val filesNumber: Int,
        val foldersNumber: Int,
        val token: String,
        val title: String? = null
)