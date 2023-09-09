package ua.edu.ontu.wdt.service.impl.permission

import ua.edu.ontu.wdt.service.IPermissionService

@Deprecated("Use lambda")
class EmptyPermissionServiceImpl : IPermissionService {

    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        onSuccess: () -> Unit,
        onRequestPermissions: (permissions: Array<out String>) -> Boolean,
        onPermissionsNotAccepted: () -> Unit
    ) = onSuccess()
}