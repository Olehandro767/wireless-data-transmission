package ua.edu.ontu.wdt.layer.client

interface IDeviceRequestListener {

    companion object {
        const val STOP = "stop"
        const val GET_INFO = "get_info"
        const  val GET_CLIPBOARD = "get_clipboard"
        const val SEND_CLIPBOARD = "send_clipboard"
        const val GET_FILE_SYSTEM = "get_file_system"
        const val SEND_FILES_OR_FOLDERS = "send_files_or_folders"
    }

    fun serve()

    fun stop()
}
