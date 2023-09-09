package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.AbstractDeviceRequestListener
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.RequestDto
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpIOFactory
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

class TcpDeviceRequestListener(
    handler: IIOSecurityHandler,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
    messageHandler: IIOSecurityHandler = UnsecureRequestResponseHandler(),
    private val _semaphore: Semaphore = Semaphore(_genericConfiguration.context.maxNumberOfConnections),
    private val _logger: ILog = EmptyLogger(),
    onEndRule: ITcpLambda = ITcpLambda {
        _logger.info("Release client: ${it.context.inetAddress.hostAddress}")
        _semaphore.release()
    },
) : AbstractDeviceRequestListener<ServerSocket, Socket>(
    _logger,
    _genericConfiguration.asyncConfiguration,
    TcpGetInfo(handler, _genericConfiguration.context, onEndRule),
    TcpSendClipboard(onEndRule),
    TcpAcceptClipboard(_genericConfiguration, messageHandler, onEndRule),
    TcpGetFileSystem(onEndRule),
    if (_genericConfiguration.context.maxThreadsForSending <= 1) TcpLegacyAcceptFileService(
        _logger,
        onEndRule,
        messageHandler,
        _genericConfiguration
    ) else TcpMultiAcceptFileService(
        _logger,
        onEndRule,
        messageHandler,
        _genericConfiguration
    ),
) {

    private var _isRun: AtomicBoolean = AtomicBoolean(false)

    override fun initListener(): ServerSocket = ServerSocket(_genericConfiguration.context.port)

    override fun validateBeforeStop(request: RequestDto<Socket>): Boolean =
        request.context.inetAddress.hostAddress == "127.0.0.1"

    override fun handleRequestAsync(isRun: AtomicBoolean, context: Socket) {
        val dataInputStream = TcpIOFactory.createMessagesReader(context)
        val message = dataInputStream.readUTF()
        manageRequest(isRun, RequestDto(message, context))
    }

    override fun createContext(listener: ServerSocket): Socket {
        val clientSocket = listener.accept()
        _semaphore.acquire()
        _logger.info("Connected: ${clientSocket.inetAddress.hostAddress}")
        return clientSocket
    }

    override fun launch(isRun: AtomicBoolean, listener: ServerSocket) {
        this._isRun = isRun
        val pointer = this

        for (index in 1.._genericConfiguration.context.numberOfListeners) {
            _logger.info("Started server thread: #$index")
            threadLoop(pointer._isRun, listener)
        }
    }

    override fun stop() {
        this._isRun.set(false)
        val socket = Socket("127.0.0.1", _genericConfiguration.context.port)
        // TODO
    }
}