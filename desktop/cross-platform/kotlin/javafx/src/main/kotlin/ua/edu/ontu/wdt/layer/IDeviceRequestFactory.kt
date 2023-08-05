package ua.edu.ontu.wdt.layer

interface IDeviceRequestFactory {

    fun createGetInfoRequestBuilder(): IGetInfoRequest

    fun createSendFileRequestBuilder(): ISendFileRequestBuilder
}