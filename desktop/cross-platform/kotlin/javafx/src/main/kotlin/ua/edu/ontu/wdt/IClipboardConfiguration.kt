package ua.edu.ontu.wdt

interface IClipboardConfiguration {

    fun writeText(text: String)

    fun readText(): String
}