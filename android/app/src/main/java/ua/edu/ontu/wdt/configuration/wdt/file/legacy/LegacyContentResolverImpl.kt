package ua.edu.ontu.wdt.configuration.wdt.file.legacy

import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import ua.edu.ontu.wdt.layer.file.IContentResolver
import ua.edu.ontu.wdt.layer.file.IFileContext
import java.io.File
import java.io.FileOutputStream

class LegacyContentResolverImpl: IContentResolver {

    override fun prepareFile(path: String): IFileContext {
        val outputFolder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        val file = File(outputFolder, path)
        return FileContext(FileOutputStream(file), file.name, file.path)
    }

    override fun readFile(): IFileContext {
        TODO()
    }
}