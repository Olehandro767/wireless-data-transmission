package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.client.RequestDto

data class RequestCommandConfiguration<T>(
    val getInfo: (RequestDto<T>) -> Unit,
    val getClipboard: (RequestDto<T>) -> Unit,
    val acceptClipboard: (RequestDto<T>) -> Unit,
    val getFileSystem: (RequestDto<T>) -> Unit,
    val acceptFileOrFolder: (RequestDto<T>) -> Unit
)
