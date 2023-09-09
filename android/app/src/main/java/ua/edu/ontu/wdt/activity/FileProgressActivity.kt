package ua.edu.ontu.wdt.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.edu.ontu.wdt.R
import ua.edu.ontu.wdt.helpful.factory.PermissionServiceFactory.createPermissionService

class FileProgressActivity : AppCompatActivity() {

    private val _permissionService = createPermissionService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_progress)
    }
}