package ua.edu.ontu.wdt.service

import androidx.activity.ComponentActivity

fun interface IPermissionService {

    fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        activity: ComponentActivity?,
        onSuccess: () -> Unit,
        onPermissionsNotAccepted: (() -> Unit)?,
    )
}