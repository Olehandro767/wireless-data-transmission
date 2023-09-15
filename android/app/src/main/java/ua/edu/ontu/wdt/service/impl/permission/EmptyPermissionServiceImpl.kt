package ua.edu.ontu.wdt.service.impl.permission

import androidx.activity.ComponentActivity
import ua.edu.ontu.wdt.service.IPermissionService

@Deprecated("Use lambda")
class EmptyPermissionServiceImpl : IPermissionService {
    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        activity: ComponentActivity?,
        onSuccess: () -> Unit,
        onPermissionsNotAccepted: (() -> Unit)?
    ) = onSuccess()
}