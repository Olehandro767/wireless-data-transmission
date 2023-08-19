package ua.edu.ontu.wdt.layer.client

interface IIOSecurityHandler {

    fun handleMessageBeforeSend(message: String): String

    fun handleAcceptedMessage(message: String): String

    fun handleBytesBeforeSend(bytes: ByteArray): ByteArray

    fun handleAcceptedBytes(bytes: ByteArray): ByteArray
}