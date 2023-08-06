package ua.edu.ontu.wdt.layer

fun interface IUiGenericConfirmMessage<T> {

    fun ask(dto: T, onAccept: () -> Unit, onCancel: () -> Unit)
}