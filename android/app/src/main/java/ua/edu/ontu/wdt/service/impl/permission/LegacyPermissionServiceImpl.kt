package ua.edu.ontu.wdt.service.impl.permission

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.SYSTEM_ALERT_WINDOW
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import ua.edu.ontu.wdt.service.IPermissionService
import ua.edu.ontu.wdt.util.PermissionUtils.checkPermission

class LegacyPermissionServiceImpl(
    private val _context: Context
) : IPermissionService { // API 23 to 32

    private val _permissions =
        arrayOf(READ_EXTERNAL_STORAGE, INTERNET, SYSTEM_ALERT_WINDOW, WRITE_EXTERNAL_STORAGE)

    private fun checkPermission(): Boolean = checkPermission(_context, *_permissions)

    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        onSuccess: () -> Unit,
        onRequestPermissions: (permissionCheckResult: Boolean, permissions: Array<out String>) -> Boolean,
        onPermissionsNotAccepted: () -> Unit,
    ) {
        val permissionCheckResult = this.checkPermission()
        when {
            permissionCheckResult -> onSuccess()
            onRequestPermissions(permissionCheckResult, _permissions) -> onSuccess()
            else -> onPermissionsNotAccepted()
        }
    }
}