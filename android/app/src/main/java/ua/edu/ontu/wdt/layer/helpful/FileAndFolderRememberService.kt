package ua.edu.ontu.wdt.layer.helpful

import java.io.File

class FileAndFolderRememberService {

    private var _files = ArrayList<File>(3)

    fun rememberFiles(vararg files: File) = _files.addAll(files)

    fun getAllFiles(): List<File> = _files

    fun cleanAllFiles() {
        _files = ArrayList(3)
    }

    fun isEmpty(): Boolean = _files.isEmpty()
}