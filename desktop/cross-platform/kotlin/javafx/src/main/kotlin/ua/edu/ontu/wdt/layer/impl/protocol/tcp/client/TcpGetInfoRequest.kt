package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.DeviceType.Companion.findType
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.GET_INFO
import ua.edu.ontu.wdt.layer.IGetInfoRequest
import ua.edu.ontu.wdt.layer.IRequestResponseHandler
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpUtils
import ua.edu.ontu.wdt.layer.utils.request.RequestUtils.prepareRequestType
import java.net.Socket

class TcpGetInfoRequest(
    private val context: IContext,
    private val messageHandler: IRequestResponseHandler,
): IGetInfoRequest {

    override fun doRequest(ip: String): GetInfoDto {
        val socket = Socket(ip, context.port)
        val messageSender = TcpUtils.createMessageSender(socket)
        val messageReader = TcpUtils.createMessagesReader(socket)
        messageSender.writeUTF(this.messageHandler.handleMessageBeforeSend(prepareRequestType(GET_INFO)))
        messageSender.flush()
        val result = this.messageHandler.handleAcceptedMessage(messageReader.readUTF()).split(',')
        return GetInfoDto(socket.inetAddress.hostAddress, result[0], result[1], findType(result[2]))
    }
}