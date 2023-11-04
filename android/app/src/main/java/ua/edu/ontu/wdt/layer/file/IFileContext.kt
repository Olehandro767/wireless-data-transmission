package ua.edu.ontu.wdt.layer.file

interface IFileContext {

    val fileName: String
    val filePath: String

    fun write(buffer: ByteArray, from: Int, length: Int)
}