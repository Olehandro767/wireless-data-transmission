package ua.edu.ontu.wdt.service.impl.permission

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import ua.edu.ontu.wdt.service.IPermissionService
import ua.edu.ontu.wdt.util.PermissionUtils.checkPermission

class Api33AndHigherPermissionServiceImpl(private val _context: Context) : IPermissionService {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val _permissions = arrayOf(
        READ_MEDIA_AUDIO, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, INTERNET
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
        activity: Activity?,
        onSuccess: () -> Unit,
        onPermissionsNotAccepted: (() -> Unit)?
    ) {
        when {
            checkPermission(_context, *_permissions) -> onSuccess()
            else -> {
                if (activity != null) {
                    for (permission in _permissions) {
                        val launcher =
                            (activity as ComponentActivity).registerForActivityResult(
                                ActivityResultContracts.RequestPermission()
                            ) { isGranted ->
                                if (!isGranted && onPermissionsNotAccepted != null) {
                                    onPermissionsNotAccepted()
                                }
                            }
                        launcher.launch(permission)
                    }
                } else if (onPermissionsNotAccepted != null) {
                    onPermissionsNotAccepted()
                }
            }
        }
    }

}