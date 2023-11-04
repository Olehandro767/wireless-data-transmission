package ua.edu.ontu.wdt.configuration.wdt

import android.annotation.SuppressLint
import android.content.Context
import ua.edu.ontu.wdt.configuration.wdt.file.legacy.LegacyContentResolverImpl
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.helpful.FileAndFolderRememberService

@SuppressLint("StaticFieldLeak")
object ApplicationGlobalContext {

    val WDT_CONFIGURATION = WdtGenericConfiguration(
        WdtContextConfiguration(),
        AsyncConfiguration(),
        LegacyContentResolverImpl(),
        ClipboardConfiguration(),
        UiConfiguration()
    )
    val GENERIC_FILE_AND_FOLDER_SERVICE = FileAndFolderRememberService()
    var ANDROID_PACKAGE_CONTEXT: Context? = null
}