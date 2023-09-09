package ua.edu.ontu.wdt.layer.ui

fun interface IUiGenericConfirmMessage<T> {

    fun ask(dto: T, onAccept: () -> Unit, onCancel: () -> Unit)
}