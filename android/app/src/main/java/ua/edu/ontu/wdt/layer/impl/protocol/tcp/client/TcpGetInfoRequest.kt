package ua.edu.ontu.wdt.layer.impl.protocol.tcp.client

import ua.edu.ontu.wdt.layer.DeviceType.Companion.findType
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.GET_INFO
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest
import ua.edu.ontu.wdt.layer.client.IGetInfoRequest.Companion.TIMEOUT_MS
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageReader
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpMessageSender
import ua.edu.ontu.wdt.layer.utils.RequestUtils.prepareRequestType
import java.net.InetSocketAddress
import java.net.Socket

class TcpGetInfoRequest(
    private val context: IContext,
    private val messageHandler: IIOSecurityHandler,
) : IGetInfoRequest {

    override fun doRequest(ip: String): GetInfoDto {
        val inetSocketAddress = InetSocketAddress(ip, context.port)
        val socket = Socket()
        socket.connect(inetSocketAddress, TIMEOUT_MS)
        val messageSender = TcpMessageSender(socket, this.messageHandler)
        val messageReader = TcpMessageReader(socket, this.messageHandler)
        messageSender.sendMessageToRemoteDevice(prepareRequestType(GET_INFO))
        val result = messageReader.readMessageFromRemoteDevice().split(',')
        return GetInfoDto(
            socket.inetAddress.hostAddress,
            result[0],
            result[1],
            findType(result[2]),
            result[3].toInt()
        )
    }
}