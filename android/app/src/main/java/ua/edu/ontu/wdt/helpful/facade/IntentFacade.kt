package ua.edu.ontu.wdt.helpful.facade

import android.content.Intent
import android.os.Parcelable
import ua.edu.ontu.wdt.helpful.factory.IntentConfigurationFactory.createIntentConfiguration

class IntentFacade(intent: Intent) {

    private val _intent = createIntentConfiguration(intent)

    fun <T : Parcelable> getParcelableArrayListExtra(name: String, type: Class<T>): List<T>? =
        _intent.getParcelableArrayListExtra(name, type)

    fun <T : Parcelable> getParcelableExtra(name: String, type: Class<T>): T? =
        _intent.getParcelableExtra(name, type)

    fun getStringExtra(name: String): String? = _intent.getStringExtra(name)
}