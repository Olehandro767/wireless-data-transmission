package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration

data class WdtConfiguration(
    val context: IContext,
    val asyncConfiguration: IAsyncConfiguration,
    val clipboardConfiguration: IClipboardConfiguration,
    val uiConfiguration: IUiObserverAndMessageConfiguration,
)