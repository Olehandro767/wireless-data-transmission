package ua.edu.ontu.wdt.util

import android.widget.Toast
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.ANDROID_PACKAGE_CONTEXT

object ToastUtils {

    fun showCompactMessage(msg: String) {
        Toast.makeText(ANDROID_PACKAGE_CONTEXT, msg, Toast.LENGTH_LONG)
    }
}