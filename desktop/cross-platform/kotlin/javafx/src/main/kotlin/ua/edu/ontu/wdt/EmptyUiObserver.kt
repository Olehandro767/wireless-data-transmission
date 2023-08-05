package ua.edu.ontu.wdt

import ua.edu.ontu.wdt.layer.IUiGenericObserver
import ua.edu.ontu.wdt.layer.IUiObserver

class EmptyUiObserver<T>: IUiGenericObserver<T>, IUiObserver {

    override fun notifyUi(dto: T) {
        // empty observer
    }

    override fun notifyUi() {
        // empty observer
    }
}