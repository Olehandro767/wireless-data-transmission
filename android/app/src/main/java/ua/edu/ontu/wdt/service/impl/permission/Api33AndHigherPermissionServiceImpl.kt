package ua.edu.ontu.wdt.service.impl.permission

import ua.edu.ontu.wdt.service.IPermissionService

class Api33AndHigherPermissionServiceImpl : IPermissionService {

    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        onSuccess: () -> Unit,
        onRequestPermissions: (permissions: Array<out String>) -> Boolean,
        onPermissionsNotAccepted: () -> Unit
    ) {
    }
}