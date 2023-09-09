package ua.edu.ontu.wdt.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.WDT_CONFIGURATION
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory
import ua.edu.ontu.wdt.layer.impl.log.StdLog

class ApplicationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) =
        createPermissionService().showPermissionsDialogIfTheyNotAcceptedAndRunCommand {
            DeviceRequestListenerFactory.createDeviceListener(
                WDT_CONFIGURATION, StdLog()
            ).serve()
        }
}