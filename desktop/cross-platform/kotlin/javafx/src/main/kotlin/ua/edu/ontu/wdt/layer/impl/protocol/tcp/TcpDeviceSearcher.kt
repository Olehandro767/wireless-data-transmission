package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import ua.edu.ontu.wdt.layer.IDeviceSearcher
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.client.IDeviceRequestFactory
import ua.edu.ontu.wdt.layer.dto.GetInfoDto
import ua.edu.ontu.wdt.layer.types.ip.Ipv4Set
import ua.edu.ontu.wdt.layer.ui.IUiGenericObserver
import ua.edu.ontu.wdt.layer.utils.ArrayUtils
import ua.edu.ontu.wdt.layer.utils.Ipv4Utils
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TcpDeviceSearcher(
    private val updateProgress: IUiGenericObserver<Byte>,
    private val addDevice: IUiGenericObserver<GetInfoDto>,
    private val isRunningObserver: IUiGenericObserver<AtomicBoolean>,
    private val deviceRequestFactory: IDeviceRequestFactory,
    private val asyncConfiguration: IAsyncConfiguration,
    private val log: ILog
) : IDeviceSearcher {

    companion object {
        const val NUMBER_OF_THREADS_FOR_SEARCH = 30
    }

    private val isRunning = AtomicBoolean(true)
    private val ipv4Sets =
        Arrays.stream(Ipv4Utils.getIpsV4ForCurrentDevice()).map { Ipv4Set(it) }.toList().toTypedArray()
    private val indexLimit =
        Arrays.stream(ipv4Sets).map { it.limit }.reduce { value1, value2 -> value1 + value2 }.orElseThrow()
    private var index = AtomicInteger(0)

    private fun handleIps(vararg ips: String) = this.asyncConfiguration.runIOOperationAsync {
        for (ip in ips) {
            if (!isRunning.get()) {
                break
            }

            this.index.set(this.index.get() + 1)
            this.updateProgress.notifyUi(((((this.index.get()).toDouble() / this.indexLimit.toDouble()) * 100).toInt() + 1).toByte())
            try {
                this.addDevice.notifyUi(
                    this.deviceRequestFactory.createGetInfoRequestBuilder().doRequest(ip)
                )
            } catch (exception: Exception) {
                this.log.info("Ignore: $ip")
            }
        }
    }


    override fun search() {
        this.isRunningObserver.notifyUi(this.isRunning)

        for (ipv4Set in this.ipv4Sets) {
            for (ips in ArrayUtils.splitArrayWithConsideration(ipv4Set.toArray(), NUMBER_OF_THREADS_FOR_SEARCH)) {
                this.handleIps(*ips)
            }
        }
    }
}