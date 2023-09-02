package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import ua.edu.ontu.wdt.layer.AbstractDeviceRequestListener
import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import ua.edu.ontu.wdt.layer.IContext
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IIOSecurityHandler
import ua.edu.ontu.wdt.layer.client.RequestDto
import ua.edu.ontu.wdt.layer.impl.handler.UnsecureRequestResponseHandler
import ua.edu.ontu.wdt.layer.impl.log.EmptyLogger
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpIOFactory
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

class TcpDeviceRequestListener(
    handler: IIOSecurityHandler,
    private val context: IContext,
    asyncConfiguration: IAsyncConfiguration,
    uiObserverConfiguration: IUiObserverAndMessageConfiguration,
    messageHandler: IIOSecurityHandler = UnsecureRequestResponseHandler(),
    private val semaphore: Semaphore = Semaphore(context.maxNumberOfConnections),
    private val logger: ILog = EmptyLogger(),
    onEndRule: ITcpLambda = ITcpLambda {
        logger.info("Release client: ${it.context.inetAddress.hostAddress}")
        semaphore.release()
    },
) : AbstractDeviceRequestListener<ServerSocket, Socket>(
    logger,
    asyncConfiguration,
    TcpGetInfo(handler, context, onEndRule),
    TcpGetClipboard(onEndRule),
    TcpSendClipboard(onEndRule),
    TcpGetFileSystem(onEndRule),
    if (context.maxThreadsForSending <= 1) TcpLegacyAcceptFileService(
        logger,
        context,
        asyncConfiguration,
        onEndRule,
        messageHandler,
        uiObserverConfiguration.createConfirmFileMessage(),
        uiObserverConfiguration.createProgressObserverForSendFileRule(),
        uiObserverConfiguration.createCancelObserverForSendFileRule(),
        uiObserverConfiguration.createBeforeSendCommonObserver(),
        uiObserverConfiguration.createProblemObserverForSendFileRule()
    ) else TcpMultiAcceptFileService(
        logger,
        context,
        asyncConfiguration,
        onEndRule,
        messageHandler,
        uiObserverConfiguration.createConfirmFileMessage(),
        uiObserverConfiguration.createProgressObserverForSendFileRule(),
        uiObserverConfiguration.createCancelObserverForSendFileRule(),
        uiObserverConfiguration.createBeforeSendCommonObserver(),
        uiObserverConfiguration.createProblemObserverForSendFileRule()
    ),
) {

    var isRun: AtomicBoolean = AtomicBoolean(false)

    override fun initListener(): ServerSocket = ServerSocket(this.context.port)

    override fun validateBeforeStop(request: RequestDto<Socket>): Boolean =
        request.context.inetAddress.hostAddress == "127.0.0.1"

    override fun handleRequestAsync(isRun: AtomicBoolean, context: Socket) {
        val dataInputStream = TcpIOFactory.createMessagesReader(context)
        val message = dataInputStream.readUTF()
        manageRequest(isRun, RequestDto(message, context))
    }

    override fun createContext(listener: ServerSocket): Socket {
        val clientSocket = listener.accept()
        semaphore.acquire()
        logger.info("Connected: ${clientSocket.inetAddress.hostAddress}")
        return clientSocket
    }

    override fun launch(isRun: AtomicBoolean, listener: ServerSocket) {
        this.isRun = isRun
        val pointer = this

        for (index in 1..context.numberOfListeners) {
            logger.info("Started server thread: #$index")
            threadLoop(pointer.isRun, listener)
        }
    }

    override fun stop() {
        this.isRun.set(false)
        val socket = Socket("127.0.0.1", context.port)
        // TODO
    }
}