package ua.edu.ontu.wdt.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ua.edu.ontu.wdt.ExtraConstants.ExtraKeys.SEND_TYPE_KEY
import ua.edu.ontu.wdt.ExtraConstants.ExtraValues.GET_CLIPBOARD_VALUE
import ua.edu.ontu.wdt.ExtraConstants.ExtraValues.SEND_CLIPBOARD_VALUE
import ua.edu.ontu.wdt.R
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.ANDROID_PACKAGE_CONTEXT
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.service.IPermissionService

class MainActivity : AppCompatActivity() {

    private lateinit var _permissionService: IPermissionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ANDROID_PACKAGE_CONTEXT = this
        _permissionService = createPermissionService(ANDROID_PACKAGE_CONTEXT!!)
    }

    override fun onStart() {
        super.onStart()
        findViewById<Button>(R.id.getClipboardButton).apply {
            setOnClickListener {
                startActivity(Intent(ANDROID_PACKAGE_CONTEXT, DeviceActivity::class.java).apply {
                    putExtra(SEND_TYPE_KEY, GET_CLIPBOARD_VALUE)
                })
            }
        }
        findViewById<Button>(R.id.sendClipboardButton).apply {
            setOnClickListener {
                startActivity(Intent(ANDROID_PACKAGE_CONTEXT, DeviceActivity::class.java).apply {
                    putExtra(SEND_TYPE_KEY, SEND_CLIPBOARD_VALUE)
                })
            }
        }
    }
}