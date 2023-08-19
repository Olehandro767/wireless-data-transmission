package ua.edu.ontu.wdt.service

import java.io.File

class FileAndFolderRememberService {

    private var files = ArrayList<File>(3)

    fun rememberFiles(vararg files: File) = this.files.addAll(files)

    fun getAllFiles(): List<File> = this.files

    fun cleanAllFiles() {
        this.files = ArrayList(3)
    }

    fun isEmpty(): Boolean = this.files.isEmpty()
}