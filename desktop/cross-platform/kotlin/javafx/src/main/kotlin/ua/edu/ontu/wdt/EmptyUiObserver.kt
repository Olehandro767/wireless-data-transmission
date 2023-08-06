package ua.edu.ontu.wdt

import ua.edu.ontu.wdt.layer.IUiGenericConfirmMessage
import ua.edu.ontu.wdt.layer.IUiGenericObserver
import ua.edu.ontu.wdt.layer.IUiObserver

class EmptyUiObserver<T>: IUiGenericObserver<T>, IUiGenericConfirmMessage<T>, IUiObserver {

    override fun notifyUi(dto: T) {
        // empty observer
    }

    override fun notifyUi() {
        // empty observer
    }

    override fun ask(dto: T, onAccept: () -> Unit, onCancel: () -> Unit) {
        // empty confirm
    }
}