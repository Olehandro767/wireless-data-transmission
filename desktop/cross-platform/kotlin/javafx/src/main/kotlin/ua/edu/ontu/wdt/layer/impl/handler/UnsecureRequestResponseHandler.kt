package ua.edu.ontu.wdt.layer.impl.handler

import ua.edu.ontu.wdt.layer.IRequestResponseHandler

class UnsecureRequestResponseHandler: IRequestResponseHandler {

    override fun handleMessageBeforeSend(message: String): String = message

    override fun handleAcceptedMessage(message: String): String = message

    override fun handleBytesBeforeSend(bytes: ByteArray): ByteArray = bytes

    override fun handleAcceptedBytes(bytes: ByteArray): ByteArray = bytes
}