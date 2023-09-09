package ua.edu.ontu.wdt.layer.impl.protocol.tcp

import ua.edu.ontu.wdt.layer.IDeviceSearcher
import ua.edu.ontu.wdt.layer.ILog
import ua.edu.ontu.wdt.layer.WdtGenericConfiguration
import ua.edu.ontu.wdt.layer.client.IDeviceRequestFactory
import ua.edu.ontu.wdt.layer.types.ip.Ipv4Set
import ua.edu.ontu.wdt.layer.utils.ArrayUtils
import ua.edu.ontu.wdt.layer.utils.Ipv4Utils
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TcpDeviceSearcher(
    private val _deviceRequestFactory: IDeviceRequestFactory,
    private val _genericConfiguration: WdtGenericConfiguration<*, *>,
    private val _log: ILog
) : IDeviceSearcher {

    private val _uiConfiguration = _genericConfiguration.uiConfiguration
    private val _isRunning = AtomicBoolean(true)
    private val _ipv4Sets =
        listOf(*Ipv4Utils.getIpsV4ForCurrentDevice()).map { Ipv4Set(it) }.toList().toTypedArray()
    private val _indexLimit =
        listOf(*_ipv4Sets).map { it.limit }.reduce { value1, value2 -> value1 + value2 }
    private var _index = AtomicInteger(0)

    private fun handleIps(vararg ips: String) =
        _genericConfiguration.asyncConfiguration.runIOOperationAsync {
            for (ip in ips) {
                if (!_isRunning.get()) {
                    break
                }

                _index.set(_index.get() + 1)
                _uiConfiguration.createUiDeviceSearchProgressObserver()
                    .notifyUi(((((_index.get()).toDouble() / _indexLimit.toDouble()) * 100).toInt() + 1).toByte())
                try {
                    _uiConfiguration.createUiNewDeviceInfoObserver().notifyUi(
                        _deviceRequestFactory.createGetInfoRequestBuilder().doRequest(ip)
                    )
                } catch (exception: Exception) {
                    _log.info("Ignore: $ip")
                }
            }
        }


    override fun search() {
        _uiConfiguration.createCancelObserver().notifyUi(_isRunning)

        for (ipv4Set in _ipv4Sets) {
            for (ips in ArrayUtils.splitArrayWithConsideration(
                ipv4Set.toArray(), _genericConfiguration.context.maxThreadsForSearching
            )) {
                this.handleIps(*ips)
            }
        }
    }
}