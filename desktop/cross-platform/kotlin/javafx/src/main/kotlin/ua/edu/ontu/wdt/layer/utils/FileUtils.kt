package ua.edu.ontu.wdt.layer.utils

import ua.edu.ontu.wdt.layer.dto.file.FileInfoDto
import ua.edu.ontu.wdt.layer.dto.file.FileRequestInfoDto
import ua.edu.ontu.wdt.layer.dto.file.SplitFilesAndFoldersDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpCommon.FILE_DELIMITER
import java.io.File
import java.util.UUID.randomUUID

object FileUtils {

    fun iterateOverFiles(onDirectory: (file: File) -> Unit, onFile: (file: File) -> Unit, vararg files: File) {
        for (item in files) {
            if (item.isDirectory) {
                onDirectory(item)
            } else {
                onFile(item)
            }
        }
    }

    fun iterateThroughTheFolderRecursively(
        onDirectory: (folder: File, rootFolder: File?) -> Unit,
        onFile: (file: File, folder: File?) -> Unit,
        rootFolder: File? = null,
        vararg folderItems: File,
    ) {
        iterateOverFiles(
            onDirectory = {
                val fileList = it.listFiles()

                if (fileList != null) {
                    onDirectory(it, rootFolder)
                    iterateThroughTheFolderRecursively(onDirectory, onFile, rootFolder, *fileList)
                }
            },
            onFile = { onFile(it, rootFolder) },
            *folderItems
        )
    }

    fun separateFilesAndFolders(vararg args: File): SplitFilesAndFoldersDto {
        val files = ArrayList<FileInfoDto>()
        val folders = ArrayList<FileInfoDto>()

        iterateOverFiles(
            onDirectory = {
                val fileList = it.listFiles()

                if (fileList != null) {
                    folders.add(FileInfoDto(it))
                    iterateThroughTheFolderRecursively(
                        onDirectory = { folder, rootFolder -> folders.add(FileInfoDto(folder, rootFolder)) },
                        onFile = { file, rootFolder -> files.add(FileInfoDto(file, rootFolder)) },
                        it,
                        *fileList
                    )
                }
            },
            onFile = { files.add(FileInfoDto(it)) },
            *args
        )

        return SplitFilesAndFoldersDto(files.toTypedArray(), folders.toTypedArray())
    }

    fun generateFileInfo(fileInfo: FileInfoDto): String =
        "${fileInfo.getPathFromRootFolderOrGetEntityName()},${fileInfo.entity.length()}"

    fun generateFilesInfoWithToken(splitFilesAndFolders: SplitFilesAndFoldersDto): String =
        "${splitFilesAndFolders.files.size}${FILE_DELIMITER}${splitFilesAndFolders.folders.size},${randomUUID()}" +
                if (splitFilesAndFolders.consistsOfOneFileOrFolder()) ",${splitFilesAndFolders.getTitleIfFileOrFolderIsSingle()}" else ""

    fun parseToFileRequestInfoDto(requestString: String): FileRequestInfoDto {
        val splitRequest = requestString.split(",")
        val filesAndFoldersInfo = splitRequest[0].split(FILE_DELIMITER)
        val filesNumber = filesAndFoldersInfo[0].toInt()
        val folderNumber = filesAndFoldersInfo[1].toInt()
        return FileRequestInfoDto(
            requestString,
            filesNumber,
            folderNumber,
            splitRequest[1],
            if ((filesNumber + folderNumber) == 1)
                splitRequest[2]
            else
                null
        )
    }
}