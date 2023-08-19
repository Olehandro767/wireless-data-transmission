package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import kotlinx.coroutines.*
import ua.edu.ontu.wdt.layer.*
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
    TcpGetInfo(handler, context, onEndRule),
    TcpGetClipboard(onEndRule),
    TcpSendClipboard(onEndRule),
    TcpGetFileSystem(onEndRule),
    if (context.maxThreadsForSending <= 1) TcpLegacyAcceptFileService(
        logger,
        context,
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
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun initListener(): ServerSocket = ServerSocket(this.context.port)

    override fun validateBeforeStop(request: RequestDto<Socket>): Boolean
            = request.context.inetAddress.hostAddress == "127.0.0.1"

    override suspend fun handleRequestAsync(isRun: AtomicBoolean, context: Socket) {
        withContext(this.ioDispatcher) {
            val dataInputStream = TcpIOFactory.createMessagesReader(context)
            val message = dataInputStream.readUTF()
            manageRequest(isRun, RequestDto(message, context))
        }
    }

    override fun createContext(listener: ServerSocket): Socket {
        val clientSocket = listener.accept()
        semaphore.acquire()
        logger.info("Connected: ${clientSocket.inetAddress.hostAddress}")
        return clientSocket
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun launch(isRun: AtomicBoolean, listener: ServerSocket) {
        this.isRun = isRun
        val pointer = this

        for (index in 1..context.numberOfListeners) {
            logger.info("Started server thread: #$index")
            GlobalScope.launch {
                threadLoop(pointer.isRun, listener)
            }
        }
    }

    override fun stop() {
        this.isRun.set(false)
        val socket = Socket("127.0.0.1", context.port)
        // TODO
    }
}