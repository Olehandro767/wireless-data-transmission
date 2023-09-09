package ua.edu.ontu.wdt.service.impl.intent

import android.content.Intent
import android.os.Parcelable

@Suppress("DEPRECATION")
class LegacyIntentImpl(private val _intent: Intent) : AbstractIntentImpl(_intent) {

    override fun <T : Parcelable> getParcelableArrayListExtra(
        name: String, type: Class<T>
    ): List<T>? = _intent.getParcelableArrayListExtra(name)

    override fun <T : Parcelable> getParcelableExtra(name: String, type: Class<T>): T? =
        _intent.getParcelableExtra(name)
}