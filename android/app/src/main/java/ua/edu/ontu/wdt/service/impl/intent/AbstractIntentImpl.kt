package ua.edu.ontu.wdt.service.impl.intent

import android.content.Intent
import ua.edu.ontu.wdt.service.IGenericIntent

abstract class AbstractIntentImpl(private val _intent: Intent) : IGenericIntent {

    override fun getStringExtra(name: String): String? = _intent.getStringExtra(name)
}