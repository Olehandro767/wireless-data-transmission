package ua.edu.ontu.wdt.layer.impl.handler

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler

class UnsecureRequestResponseHandler: IIOSecurityHandler {

    override fun handleMessageBeforeSend(message: String): String = message

    override fun handleAcceptedMessage(message: String): String = message

    override fun handleBytesBeforeSend(bytes: ByteArray): ByteArray = bytes

    override fun handleAcceptedBytes(bytes: ByteArray): ByteArray = bytes
}