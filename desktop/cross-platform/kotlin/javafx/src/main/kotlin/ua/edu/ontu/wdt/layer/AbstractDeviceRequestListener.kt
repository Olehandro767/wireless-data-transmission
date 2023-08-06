package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.GET_CLIPBOARD
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.GET_FILE_SYSTEM
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.GET_INFO
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.SEND_CLIPBOARD
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.SEND_FILES_OR_FOLDERS
import ua.edu.ontu.wdt.layer.IDeviceRequestListener.Companion.STOP
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractDeviceRequestListener<T, C,>(
    private val logger: ILog,
    private val getInfo: (RequestDto<C>) -> Unit,
    private val getClipboard: (RequestDto<C>) -> Unit,
    private val sendClipboard: (RequestDto<C>) -> Unit,
    private val getFileSystem: (RequestDto<C>) -> Unit,
    private val acceptFileOrFolder: (RequestDto<C>) -> Unit,
) : IDeviceRequestListener {

    abstract fun initListener(): T // example: get server socket

    abstract fun launch(isRun: AtomicBoolean, listener: T) // run threadLoop method

    abstract fun createContext(listener: T): C // example: get accepted client

    abstract suspend fun handleRequestAsync(isRun: AtomicBoolean, context: C) // example: accept file

    abstract fun validateBeforeStop(request: RequestDto<C>): Boolean

    suspend fun threadLoop(isRun: AtomicBoolean, listener: T) {
        while (isRun.get()) {
            this.handleRequestAsync(isRun, this.createContext(listener))
        }
    }

    fun manageRequest(isRun: AtomicBoolean, request: RequestDto<C>) {
        val command = request.message.substring(request.message.indexOf('(') + 1, request.message.indexOf(')'))

        when (command) {
            GET_INFO -> {
                this.getInfo(request)
            }
            SEND_FILES_OR_FOLDERS -> {
                this.acceptFileOrFolder(request)
            }
            GET_CLIPBOARD -> {
                this.getClipboard(request)
            }
            SEND_CLIPBOARD -> {
                this.sendClipboard(request)
            }
            GET_FILE_SYSTEM -> {
                this.getFileSystem(request)
            }
            STOP -> {
                if (this.validateBeforeStop(request)) {
                    isRun.set(false)
                    this.stop()
                }
            }
        }
    }

    override fun serve() {
        val listener = this.initListener()
        this.logger.info("Device listener started")
        launch(AtomicBoolean(true), listener)
    }
}