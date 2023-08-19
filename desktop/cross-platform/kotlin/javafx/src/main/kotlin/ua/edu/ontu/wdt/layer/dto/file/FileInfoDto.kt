package ua.edu.ontu.wdt.layer.dto.file

import java.io.File

data class FileInfoDto(val entity: File, val entryPointFolder: File? = null) {

    fun getPathFromRootFolderOrGetEntityName(): String = if (this.entryPointFolder != null)
        this.entity.absolutePath.substring(this.entity.absolutePath.indexOf(this.entryPointFolder.name))
    else
        this.entity.name
}
