package ua.edu.ontu.wdt.activity

import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SEND_MULTIPLE
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.EXTRA_TEXT
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.edu.ontu.wdt.ExtraConstants.ExtraKeys.SEND_TYPE_KEY
import ua.edu.ontu.wdt.ExtraConstants.ExtraValues.SEND_CLIPBOARD_VALUE
import ua.edu.ontu.wdt.ExtraConstants.ExtraValues.SEND_FILE_VALUE
import ua.edu.ontu.wdt.R
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.ANDROID_PACKAGE_CONTEXT
import ua.edu.ontu.wdt.handler.ApplicationBootLifeCycleHandler
import ua.edu.ontu.wdt.helpful.facade.IntentFacade
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.service.IPermissionService
import ua.edu.ontu.wdt.system.ApplicationBroadcastReceiver
import ua.edu.ontu.wdt.util.ToastUtils
import java.io.File

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var _intentFacade: IntentFacade
    private lateinit var _permissionService: IPermissionService
    private lateinit var _bootLifeCycleHandler: ApplicationBootLifeCycleHandler

    private fun switchToSendingFiles() {
        this.startActivity(Intent(ANDROID_PACKAGE_CONTEXT, DeviceActivity::class.java).apply {
            putExtra(SEND_TYPE_KEY, SEND_FILE_VALUE)
        })
    }

    private fun switchToSendingClipboard() {
        this.startActivity(Intent(ANDROID_PACKAGE_CONTEXT, DeviceActivity::class.java).apply {
            putExtra(SEND_TYPE_KEY, SEND_CLIPBOARD_VALUE)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        ANDROID_PACKAGE_CONTEXT = this.baseContext
        _intentFacade = IntentFacade(this.intent)
        _permissionService = createPermissionService(ANDROID_PACKAGE_CONTEXT!!)
        _bootLifeCycleHandler = ApplicationBootLifeCycleHandler(ANDROID_PACKAGE_CONTEXT!!)
    }

    override fun onStart() {
        super.onStart()
        _permissionService.showPermissionsDialogIfTheyNotAcceptedAndRunCommand(this, onSuccess = {
            this.sendBroadcast(
                Intent(
                    ANDROID_PACKAGE_CONTEXT,
                    ApplicationBroadcastReceiver::class.java
                ), INTERNET
            )
            when (this.intent.action) {
                ACTION_SEND_MULTIPLE -> {
                    _intentFacade.getParcelableArrayListExtra(EXTRA_STREAM, Uri::class.java)
                        ?.map { File(it.path!!) }?.let {
                            _bootLifeCycleHandler.onAcceptedFiles(*it.toTypedArray())
                            this.switchToSendingFiles()
                        }
                }

                ACTION_SEND -> {
                    _intentFacade.getStringExtra(EXTRA_TEXT)?.let {
                        _bootLifeCycleHandler.onClipboard(it)
                        this.switchToSendingClipboard()
                    }
                    _intentFacade.getParcelableExtra(EXTRA_STREAM, Uri::class.java)?.let {
                        _bootLifeCycleHandler.onAcceptedFiles(File(it.path!!))
                        this.switchToSendingFiles()
                    }
                }

                else -> this.startActivity(
                    Intent(
                        ANDROID_PACKAGE_CONTEXT,
                        MainActivity::class.java
                    )
                )
            }
        }, onPermissionsNotAccepted = {
            ToastUtils.showCompactMessage("Permissions issues")
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}