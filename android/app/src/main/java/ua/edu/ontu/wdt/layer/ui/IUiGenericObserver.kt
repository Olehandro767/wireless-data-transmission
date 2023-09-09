package ua.edu.ontu.wdt.layer.ui

fun interface IUiGenericObserver<T> {

    fun notifyUi(dto: T)
}