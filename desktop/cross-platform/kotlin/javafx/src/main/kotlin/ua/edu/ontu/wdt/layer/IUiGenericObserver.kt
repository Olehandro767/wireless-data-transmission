package ua.edu.ontu.wdt.layer

fun interface IUiGenericObserver<T> {

    fun notifyUi(dto: T)
}