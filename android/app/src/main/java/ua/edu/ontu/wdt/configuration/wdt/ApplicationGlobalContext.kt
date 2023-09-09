package ua.edu.ontu.wdt.configuration.wdt

import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.helpful.FileAndFolderRememberService

object ApplicationGlobalContext {

    val WDT_CONFIGURATION = WdtGenericConfiguration(
        WdtContextConfiguration(), AsyncConfiguration(), ClipboardConfiguration(), UiConfiguration()
    )
    val GENERIC_FILE_AND_FOLDER_SERVICE = FileAndFolderRememberService()
}