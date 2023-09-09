package ua.edu.ontu.wdt.layer.dto.file

data class SplitFilesAndFoldersDto(
    val files: Array<out FileInfoDto>,
    val folders: Array<out FileInfoDto>
) {

    fun consistsOfOneFileOrFolder(): Boolean = (this.files.size + this.folders.size) == 1

    fun getTitleIfFileOrFolderIsSingle(): String {
        if (this.files.size == 1) {
            return this.files[0].entity.name
        }

        return this.folders[0].entity.name
    }
}