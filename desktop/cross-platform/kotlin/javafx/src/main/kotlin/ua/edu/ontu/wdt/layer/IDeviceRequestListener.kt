package ua.edu.ontu.wdt.layer

interface IDeviceRequestListener {

    companion object {
        val STOP = "stop"
        val GET_INFO = "get_info"
        val GET_CLIPBOARD = "get_clipboard"
        val SEND_CLIPBOARD = "send_clipboard"
        val GET_FILE_SYSTEM = "get_file_system"
        val ACCEPT_FILE_OR_FOLDER = "accept_file_or_folder"
    }

    fun serve()

    fun stop()
}
