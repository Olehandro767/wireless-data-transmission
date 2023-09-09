package ua.edu.ontu.wdt.layer

interface IClipboardConfiguration {

    companion object {
        const val STRING_DATA = "string_data"
    }

    fun identifyTypeOfData(): String

    fun writeText(text: String)

    fun readText(): String
}