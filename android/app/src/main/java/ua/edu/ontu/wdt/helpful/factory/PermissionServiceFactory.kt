package ua.edu.ontu.wdt.helpful.factory

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import ua.edu.ontu.wdt.service.IPermissionService
import ua.edu.ontu.wdt.service.impl.permission.Api33AndHigherPermissionServiceImpl
import ua.edu.ontu.wdt.service.impl.permission.LegacyPermissionServiceImpl

object PermissionServiceFactory {

    fun createPermissionService(context: Context): IPermissionService = when {
        (SDK_INT >= TIRAMISU) -> Api33AndHigherPermissionServiceImpl()
        (SDK_INT >= M) -> LegacyPermissionServiceImpl(context)
        else -> (IPermissionService { onSuccess, _, _ -> onSuccess() })
    }

    fun createPermissionRequest(activity: ComponentActivity): (permissions: Array<out String>) -> Boolean =
        when {
            (SDK_INT >= M) -> ({
                var result = true
                val launcher = activity.registerForActivityResult(RequestPermission()) { isGranted ->
                    if (result) {
                        result = isGranted
                    }
                }
                for (permission in it) {
                    launcher.launch(permission)
                }
                result
            })

            else -> ({ true })
        }
}