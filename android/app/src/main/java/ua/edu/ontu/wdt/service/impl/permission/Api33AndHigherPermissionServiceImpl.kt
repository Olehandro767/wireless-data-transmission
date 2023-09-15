package ua.edu.ontu.wdt.service.impl.permission

import androidx.activity.ComponentActivity
import ua.edu.ontu.wdt.service.IPermissionService

class Api33AndHigherPermissionServiceImpl : IPermissionService {

    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        activity: ComponentActivity?,
        onSuccess: () -> Unit,
        onPermissionsNotAccepted: (() -> Unit)?
    ) {
        TODO("Not yet implemented")
    }

}