package ua.edu.ontu.wdt.layer

interface IRequestResponseHandler {

    fun handleMessageBeforeSend(message: String): String

    fun handleAcceptedMessage(message: String): String

    fun handleBytesBeforeSend(bytes: ByteArray): ByteArray

    fun handleAcceptedBytes(bytes: ByteArray): ByteArray
}