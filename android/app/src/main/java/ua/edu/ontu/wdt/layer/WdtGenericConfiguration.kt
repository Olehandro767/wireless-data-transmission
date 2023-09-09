package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration

data class WdtGenericConfiguration<CTX : IContext, UI : IUiObserverAndMessageConfiguration>(
    val context: CTX,
    val asyncConfiguration: IAsyncConfiguration,
    val clipboardConfiguration: IClipboardConfiguration,
    val uiConfiguration: UI,
)