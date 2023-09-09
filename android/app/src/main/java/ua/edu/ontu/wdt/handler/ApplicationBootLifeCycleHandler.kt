package ua.edu.ontu.wdt.handler

import android.content.Context
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.GENERIC_FILE_AND_FOLDER_SERVICE
import java.io.File

class ApplicationBootLifeCycleHandler(
    private val _context: Context,
) {
    fun onAcceptedFiles(vararg files: File) = GENERIC_FILE_AND_FOLDER_SERVICE.rememberFiles(*files)

    fun onClipboard(data: Any) {}
}