package ua.edu.ontu.wdt.configuration

import ua.edu.ontu.wdt.layer.ILog

class StdLogger : ILog { // for tests

    override fun warn(msg: String) {
        println(msg)
    }

    override fun warn(msg: String, exception: Exception) {
        println(msg)
        println(exception.message)
    }

    override fun info(msg: String) {
        println(msg)
    }

    override fun info(msg: String, exception: Exception) {
        println(msg)
        println(exception.message)
    }

    override fun debug(msg: String) {
        println(msg)
    }

    override fun debug(msg: String, exception: Exception) {
        println(msg)
        println(exception.message)
    }

    override fun error(msg: String) {
        println(msg)
    }

    override fun error(msg: String, exception: Exception) {
        println(msg)
        println(exception.message)
    }
}