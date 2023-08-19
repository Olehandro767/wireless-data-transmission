package ua.edu.ontu.wdt.layer.impl.handler

import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler

class SecureRequestResponseHandle: IIOSecurityHandler {

    override fun handleMessageBeforeSend(message: String): String {
        TODO("Not yet implemented")
    }

    override fun handleAcceptedMessage(message: String): String {
        TODO("Not yet implemented")
    }

    override fun handleBytesBeforeSend(bytes: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }

    override fun handleAcceptedBytes(bytes: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
}