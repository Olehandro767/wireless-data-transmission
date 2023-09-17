package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.GET_CLIPBOARD
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.GET_FILE_SYSTEM
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.GET_INFO
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.SEND_CLIPBOARD
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.SEND_FILES_OR_FOLDERS
import ua.edu.ontu.wdt.layer.client.IDeviceRequestListener.Companion.STOP
import ua.edu.ontu.wdt.layer.client.RequestDto
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractDeviceRequestListener<T, C>(
    private val _logger: ILog,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
    private val _requestCommandConfiguration: RequestCommandConfiguration<C>,
) : IDeviceRequestListener {

    abstract fun initListener(): T // example: get server socket

    abstract fun launch(isRun: AtomicBoolean, listener: T) // run threadLoop method

    abstract fun createContext(listener: T): C // example: get accepted client

    abstract fun handleRequestAsync(isRun: AtomicBoolean, context: C) // example: accept file

    abstract fun validateBeforeStop(request: RequestDto<C>): Boolean

    fun threadLoop(isRun: AtomicBoolean, listener: T) {
        _genericConfiguration.asyncConfiguration.runIOOperationAsync {
            while (isRun.get()) {
                try {
                    this.handleRequestAsync(isRun, this.createContext(listener))
                } catch (exception: Exception) {
                    _genericConfiguration.uiConfiguration.createProblemObserverForClientListener()
                        .notifyUi(exception)
                    _logger.error(exception.toString(), exception)
                }
            }
        }
    }

    fun manageRequest(isRun: AtomicBoolean, request: RequestDto<C>) {
        val command = request.message.substring(
            request.message.indexOf('(') + 1, request.message.indexOf(')')
        )

        when (command) {
            GET_INFO -> {
                _logger.info("$GET_INFO: to $request")
                _requestCommandConfiguration.getInfo(request)
            }

            SEND_FILES_OR_FOLDERS -> {
                _requestCommandConfiguration.acceptFileOrFolder(request)
            }

            GET_CLIPBOARD -> {
                _requestCommandConfiguration.getClipboard(request)
            }

            SEND_CLIPBOARD -> {
                _requestCommandConfiguration.acceptClipboard(request)
            }

            GET_FILE_SYSTEM -> {
                _requestCommandConfiguration.getFileSystem(request)
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
        _logger.info("Device listener started")
        launch(AtomicBoolean(true), listener)
    }
}