package ua.edu.ontu.wdt.layer.impl.protocol.tcp.listener

import kotlinx.coroutines.*
import ua.edu.ontu.wdt.layer.*
import ua.edu.ontu.wdt.layer.impl.protocol.tcp.TcpUtils
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

class TcpDeviceRequestListener(
    handler: IRequestResponseHandler,
    private val context: IContext,
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
    TcpAcceptFileService(onEndRule),
) {

    companion object {
        val CANCELLED_REQUEST_STATUS = 41
        val REQUEST_ACCEPTED = 2
    }

    var isRun: AtomicBoolean = AtomicBoolean(false)
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun initListener(): ServerSocket = ServerSocket(this.context.port)

    override fun validateBeforeStop(request: RequestDto<Socket>): Boolean
            = request.context.inetAddress.hostAddress == "127.0.0.1"

    override suspend fun handleRequestAsync(isRun: AtomicBoolean, context: Socket) {
        withContext(this.ioDispatcher) {
            val dataInputStream = TcpUtils.createMessagesReader(context)
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