package ua.edu.ontu.wdt.configuration.wdt.file.legacy

import ua.edu.ontu.wdt.layer.file.IFileContext
import java.io.OutputStream

class FileContext(
    private val _outputStream: OutputStream,
    override val fileName: String,
    override val filePath: String
) : IFileContext {

    override fun write(buffer: ByteArray, from: Int, length: Int) =
        _outputStream.write(buffer, from, length)
}