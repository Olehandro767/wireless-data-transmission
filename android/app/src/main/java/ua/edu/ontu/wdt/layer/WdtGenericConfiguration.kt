package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.file.IContentResolver
import ua.edu.ontu.wdt.layer.ui.IUiObserverAndMessageConfiguration

data class WdtGenericConfiguration<CTX : IContext, UI : IUiObserverAndMessageConfiguration>(
    val context: CTX,
    val asyncConfiguration: IAsyncConfiguration,
    val contentResolver: IContentResolver,
    val clipboardConfiguration: IClipboardConfiguration,
    val uiConfiguration: UI,
)