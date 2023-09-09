package ua.edu.ontu.wdt.layer

interface ILog {

    fun warn(msg: String)

    fun warn(msg: String, exception: Exception)

    fun info(msg: String)

    fun info(msg: String, exception: Exception)

    fun debug(msg: String)

    fun debug(msg: String, exception: Exception)

    fun error(msg: String)

    fun error(msg: String, exception: Exception)
}