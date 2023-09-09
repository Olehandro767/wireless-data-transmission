package ua.edu.ontu.wdt.util

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission

object PermissionUtils {

    fun checkPermission(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }
}