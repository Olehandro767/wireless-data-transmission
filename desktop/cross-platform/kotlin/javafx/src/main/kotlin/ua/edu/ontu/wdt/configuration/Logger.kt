package ua.edu.ontu.wdt.configuration

import org.apache.logging.log4j.LogManager.getLogger
import ua.edu.ontu.wdt.layer.ILog

class Logger<T>(
    type: Class<T>? = null,
    name: String? = null,
) : ILog {

    private val log = getLogger(type ?: (name ?: throw IllegalAccessException("Fill args")))

    override fun warn(msg: String) = this.log.warn(msg)

    override fun warn(msg: String, exception: Exception) = this.log.warn(msg, exception)

    override fun info(msg: String) {
        this.log.info(msg)
        println(msg)
    }

    override fun info(msg: String, exception: Exception) = this.log.info(msg, exception)

    override fun debug(msg: String) = this.log.debug(msg)

    override fun debug(msg: String, exception: Exception) = this.log.debug(msg, exception)

    override fun error(msg: String) = this.log.error(msg)

    override fun error(msg: String, exception: Exception) = this.log.error(msg, exception)
}