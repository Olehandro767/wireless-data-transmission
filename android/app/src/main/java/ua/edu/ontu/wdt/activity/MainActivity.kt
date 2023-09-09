package ua.edu.ontu.wdt.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.edu.ontu.wdt.R
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService
import ua.edu.ontu.wdt.service.IPermissionService

class MainActivity : AppCompatActivity() {

    private lateinit var _permissionService: IPermissionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _permissionService = createPermissionService()
    }
}