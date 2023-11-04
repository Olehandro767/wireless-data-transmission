package ua.edu.ontu.wdt.service

import android.app.Activity
import androidx.activity.ComponentActivity

fun interface IPermissionService {

    fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        activity: Activity?,
        onSuccess: () -> Unit,
        onPermissionsNotAccepted: (() -> Unit)?,
    )
}