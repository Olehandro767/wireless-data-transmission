package ua.edu.ontu.wdt.layer

class EmptyLogger: ILog {

    override fun warn(msg: String) {
        // empty
    }

    override fun warn(msg: String, exception: Exception) {
        // empty
    }

    override fun info(msg: String) {
        // empty
    }

    override fun info(msg: String, exception: Exception) {
        // empty
    }

    override fun debug(msg: String) {
        // empty
    }

    override fun debug(msg: String, exception: Exception) {
        // empty
    }

    override fun error(msg: String) {
        // empty
    }

    override fun error(msg: String, exception: Exception) {
        // empty
    }
}