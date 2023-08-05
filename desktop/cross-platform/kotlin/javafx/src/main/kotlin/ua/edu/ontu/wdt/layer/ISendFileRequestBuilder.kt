package ua.edu.ontu.wdt.layer

import java.io.File

fun interface ISendFileRequestBuilder {

    fun doRequest(ip: String, vararg files: File)
}