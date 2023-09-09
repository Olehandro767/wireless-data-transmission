package ua.edu.ontu.wdt.layer

interface IClipboardConfiguration {

    fun writeText(text: String)

    fun readText(): String
}