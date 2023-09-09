package ua.edu.ontu.wdt.layer.client

import java.io.File

interface ISendFileRequestBuilder : IRequestBuilder, IIpBuilderSetter<ISendFileRequestBuilder> {

    fun files(vararg files: File): ISendFileRequestBuilder

    @Deprecated("")
    fun doRequest(ip: String, vararg files: File)
}