package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.DeviceType.Companion.findType
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.GET_INFO
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpIOFactory
import ua.edu.ontu.wdt.layer.utils.RequestUtils.prepareRequestType
import java.net.Socket

class TcpGetInfoRequest(
    private val context: IContext,
    private val messageHandler: IIOSecurityHandler,
) : IGetInfoRequest {

    override fun doRequest(ip: String): GetInfoDto {
        val socket = Socket(ip, context.port)
        val messageSender = TcpIOFactory.createMessageSender(socket)
        val messageReader = TcpIOFactory.createMessagesReader(socket)
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(prepareRequestType(GET_INFO)))
        messageSender.flush()
        val result = this.messageHandler.handleAcceptedMessage(messageReader.readUTF()).split(',')
        return GetInfoDto(socket.inetAddress.hostAddress, result[0], result[1], findType(result[2]), result[3].toInt())
    }
}