package ua.edu.ontu.wdt.service.impl.permission

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.SYSTEM_ALERT_WINDOW
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import ua.edu.ontu.wdt.service.IPermissionService
import ua.edu.ontu.wdt.util.PermissionUtils.checkPermission

class LegacyPermissionServiceImpl(
    private val _context: Context
) : IPermissionService { // API 23 to 32

    private val _permissions =
        arrayOf(READ_EXTERNAL_STORAGE, INTERNET, SYSTEM_ALERT_WINDOW, WRITE_EXTERNAL_STORAGE)

    private fun checkPermission(): Boolean = checkPermission(_context, *_permissions)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        activity: ComponentActivity?,
        onSuccess: () -> Unit,
        onPermissionsNotAccepted: (() -> Unit)?,
    ) {
        val permissionCheckResult = this.checkPermission()
        when {
            permissionCheckResult -> onSuccess()
            else -> {
                if (activity != null) {
                    val launcher =
                        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                            if (!isGranted && onPermissionsNotAccepted != null) {
                                onPermissionsNotAccepted()
                            }
                        }

                    for (permission in _permissions) {
                        launcher.launch(permission)
                    }
                } else if (onPermissionsNotAccepted != null) {
                    onPermissionsNotAccepted()
                }
            }
        }
    }
}