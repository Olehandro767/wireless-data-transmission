package ua.edu.ontu.wdt.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import ua.edu.ontu.wdt.ExtraConstants.ExtraKeys.SEND_TYPE_KEY
import ua.edu.ontu.wdt.ExtraConstants.ExtraValues.SEND_CLIPBOARD_VALUE
import ua.edu.ontu.wdt.ExtraConstants.ExtraValues.SEND_FILE_VALUE
import ua.edu.ontu.wdt.R
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.GENERIC_FILE_AND_FOLDER_SERVICE
import ua.edu.ontu.wdt.configuration.wdt.ApplicationGlobalContext.WDT_CONFIGURATION
import ua.edu.ontu.wdt.configuration.wdt.UiConfiguration
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.layer.factory.DeviceRequestAbstractFactory.createDeviceRequestFactory
import ua.edu.ontu.wdt.layer.factory.DeviceSearcherFactory
import ua.edu.ontu.wdt.layer.impl.log.StdLog
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.service.IPermissionService

class DeviceActivity : AppCompatActivity() {

    private lateinit var _permissionService: IPermissionService
    private lateinit var _uiConfiguration: UiConfiguration
    private lateinit var _progressBar: ProgressBar
    private lateinit var _deviceList: LinearLayout
    private var _progress = 0 // max = 100

    @Synchronized
    private fun updateProgressBar(progress: Byte) {
        if (_progress < progress) {
            _progress = if (progress > 100) 100 else progress.toInt()
            runOnUiThread {
                _progressBar.progress = _progress
            }
        }
    }

    private fun onDeviceClicked(ip: String): OnClickListener =
        when (this.intent.extras!!.getString(SEND_TYPE_KEY)!!) {
            SEND_FILE_VALUE -> OnClickListener {
                WDT_CONFIGURATION.asyncConfiguration.runAsync {
                    createDeviceRequestFactory(WDT_CONFIGURATION).createSendFileRequestBuilder()
                        .files(
                            *GENERIC_FILE_AND_FOLDER_SERVICE.getAllFiles().toTypedArray()
                        ).ip(ip).build().doRequest()
                }
            }

            SEND_CLIPBOARD_VALUE -> OnClickListener {
                WDT_CONFIGURATION.asyncConfiguration.runAsync {
                    createDeviceRequestFactory(WDT_CONFIGURATION).createSendClipboardRequestBuilder()
                        .ip(ip).build().doRequest()
                }
            }

            else -> throw IllegalArgumentException("Extras issues")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        _permissionService = createPermissionService(this)
        _uiConfiguration = WDT_CONFIGURATION.uiConfiguration
        _progressBar = findViewById(R.id.deviceSearchProgressBar)
        _deviceList = findViewById(R.id.deviceSearchListContent)
    }

    override fun onStart() {
        super.onStart()
        _permissionService.showPermissionsDialogIfTheyNotAcceptedAndRunCommand(this, onSuccess = {
            DeviceSearcherFactory.createDeviceSearcher(WDT_CONFIGURATION, StdLog(javaClass))
                .search()
            _uiConfiguration.onDeviceSearchProgressObserver = IUiGenericObserver {
                this.updateProgressBar(it)
            }
            _uiConfiguration.onNewDeviceObserver = IUiGenericObserver {
                runOnUiThread {
                    _deviceList.addView(Button(this).apply { // TODO improvements
                        @SuppressLint("SetTextI18n")
                        text = "${it.deviceName} (${it.userName})"
                        setOnClickListener(onDeviceClicked(it.ip))
                    })
                }
            }
        }, null)
    }
}