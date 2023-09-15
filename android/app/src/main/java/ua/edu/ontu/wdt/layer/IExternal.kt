package ua.edu.ontu.wdt.layer

@Deprecated("redundant")
interface IExternal {

    fun createConfirm(msg: String, onConfirm: Unit, onCancel: Unit)

    fun fileOperationStatus(fileName: String, size: Long)

    fun statusError(msg: String)

    fun statusOk(msg: String)
}