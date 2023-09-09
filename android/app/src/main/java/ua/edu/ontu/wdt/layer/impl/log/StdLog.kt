package ua.edu.ontu.wdt.layer.impl.log

import android.util.Log
import ua.edu.ontu.wdt.layer.ILog

class StdLog<T>(tag: Class<T>) : ILog {

    private val _tag = tag.simpleName

    override fun warn(msg: String) {
        Log.w(_tag, msg)
    }

    override fun warn(msg: String, exception: Exception) {
        Log.w(_tag, msg, exception)
    }

    override fun info(msg: String) {
        Log.i(_tag, msg)
    }

    override fun info(msg: String, exception: Exception) {
        Log.i(_tag, msg, exception)
    }

    override fun debug(msg: String) {
        Log.d(_tag, msg)
    }

    override fun debug(msg: String, exception: Exception) {
        Log.d(_tag, msg, exception)
    }

    override fun error(msg: String) {
        Log.e(_tag, msg)
    }

    override fun error(msg: String, exception: Exception) {
        Log.e(_tag, msg, exception)
    }
}