package ua.edu.ontu.wdt.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.ANDROID_PACKAGE_CONTEXT
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.WDT_CONFIGURATION
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory.createDeviceListener
import ua.edu.ontu.wdt.layer.impl.log.StdLog

class ApplicationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if (ANDROID_PACKAGE_CONTEXT == null) {
                ANDROID_PACKAGE_CONTEXT = context
            }

            createPermissionService(ANDROID_PACKAGE_CONTEXT!!).showPermissionsDialogIfTheyNotAcceptedAndRunCommand(
                null,
                onSuccess = {
                    WDT_CONFIGURATION.asyncConfiguration.runIOOperationAsync {
                        val listener = createDeviceListener(
                            WDT_CONFIGURATION, StdLog(javaClass)
                        )

                        if (!listener.isRunning) {
                            listener.serve()
                        }
                    }
                },
                onPermissionsNotAccepted = {
                    // TODO
                })
        }
    }
}