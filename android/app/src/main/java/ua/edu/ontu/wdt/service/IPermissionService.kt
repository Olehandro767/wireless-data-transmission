package ua.edu.ontu.wdt.service

fun interface IPermissionService {

    fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        onSuccess: () -> Unit,
        onRequestPermissions: (permissionCheckResult: Boolean, permissions: Array<out String>) -> Boolean,
        onPermissionsNotAccepted: () -> Unit,
    )
}