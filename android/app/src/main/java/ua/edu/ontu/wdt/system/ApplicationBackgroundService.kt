package ua.edu.ontu.wdt.system

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.layer.factory.DeviceRequestListenerFactory
import ua.edu.ontu.wdt.layer.impl.log.StdLog
import ua.edu.ontu.wdt.service.IPermissionService

class ApplicationBackgroundService : Service() {

    private lateinit var _permissionService: IPermissionService

    override fun onCreate() {
        super.onCreate()
        _permissionService = createPermissionService(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        _permissionService.showPermissionsDialogIfTheyNotAcceptedAndRunCommand(null, onSuccess = {
            DeviceRequestListenerFactory.createDeviceListener(
                ApplicationGlobalContext.WDT_CONFIGURATION,
                StdLog(ApplicationBackgroundService::class.java)
            ).serve()
        }, null)
        return super.onStartCommand(intent, flags, startId)
    }

}