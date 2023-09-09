package ua.edu.ontu.wdt.layer.impl.log

import ua.edu.ontu.wdt.layer.ILog

class StdLog : ILog {

    override fun warn(msg: String) {
        println(msg)
    }

    override fun warn(msg: String, exception: Exception) {
        println(msg)
    }

    override fun info(msg: String) {
        println(msg)
    }

    override fun info(msg: String, exception: Exception) {
        println(msg)
    }

    override fun debug(msg: String) {
        println(msg)
    }

    override fun debug(msg: String, exception: Exception) {
        println(msg)
    }

    override fun error(msg: String) {
        println(msg)
    }

    override fun error(msg: String, exception: Exception) {
        println(msg)
    }
}