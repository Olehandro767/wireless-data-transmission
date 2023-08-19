package ua.edu.ontu.wdt.layer

interface IExternal {

    fun createConfirm(msg: String, onConfirm: Unit, onCancel: Unit)

    fun fileOperationStatus(fileName: String, size: Long)

    fun statusError(msg: String)

    fun statusOk(msg: String)
}