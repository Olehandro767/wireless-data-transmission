package ua.edu.ontu.wdt.helpful.factory

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import ua.edu.ontu.wdt.service.IGenericIntent
import ua.edu.ontu.wdt.service.impl.intent.LegacyIntentImpl
import ua.edu.ontu.wdt.service.impl.intent.ModernIntentImpl

object IntentConfigurationFactory {

    fun createIntentConfiguration(intent: Intent): IGenericIntent {
        if (SDK_INT >= TIRAMISU) {
            return ModernIntentImpl(intent)
        } else {
            return LegacyIntentImpl(intent)
        }
    }
}