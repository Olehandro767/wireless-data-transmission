package ua.edu.ontu.wdt.service

import java.io.FileInputStream
import java.util.*

class UiLocalizationService(
    applicationContext: ApplicationContext,
) {

    companion object {
        const val LOCALIZATION_FILE_EXTENSION = ".properties"
    }

    private fun chooseLocalization(): String = when (Locale.getDefault().language) {
        "ua" -> "ua"
        else -> "en"
    }

    private val language = Properties().run {
        val path = "${applicationContext.localizationFolder}/${chooseLocalization()}$LOCALIZATION_FILE_EXTENSION"
        load(FileInputStream(path))
        this
    }

    fun getByKey(key: String): String = this.language.getProperty(key)
}