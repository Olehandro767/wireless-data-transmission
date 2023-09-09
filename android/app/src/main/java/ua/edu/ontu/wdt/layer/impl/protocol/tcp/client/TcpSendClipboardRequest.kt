package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.IClipboardConfiguration.Companion.STRING_DATA
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.SEND_CLIPBOARD
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler


class TcpSendClipboardRequest(
    ip: String,
    ioHandler: IIOSecurityHandler,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>
) : AbstractTcpRequest(
    ip, ioHandler, _genericConfiguration
) {

    override val requestType: String = SEND_CLIPBOARD

    override fun doTcpRequest() {
        val typeOfData = _genericConfiguration.clipboardConfiguration.identifyTypeOfData()
        // sending type of data
        this.messageSender.sendMessageToRemoteDevice(typeOfData)

        when (typeOfData) {
            STRING_DATA -> this.messageSender
                .sendMessageToRemoteDevice(_genericConfiguration.clipboardConfiguration.readText())
        }

        _genericConfiguration.uiConfiguration.createFinishObserverForSendClipboardRule().notifyUi()
    }
}