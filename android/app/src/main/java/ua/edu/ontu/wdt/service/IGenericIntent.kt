package ua.edu.ontu.wdt.service

import android.os.Parcelable

interface IGenericIntent {

    fun <T : Parcelable> getParcelableArrayListExtra(name: String, type: Class<T>): List<T>?

    fun <T : Parcelable> getParcelableExtra(name: String, type: Class<T>): T?

    fun getStringExtra(name: String): String?
}