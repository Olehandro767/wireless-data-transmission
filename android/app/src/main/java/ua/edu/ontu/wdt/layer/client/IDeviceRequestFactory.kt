package ua.edu.ontu.wdt.layer.client

interface IDeviceRequestFactory {

    fun createGetInfoRequestBuilder(): IGetInfoRequest

    fun createSendFileRequestBuilder(): ISendFileRequestBuilder
}