package ua.edu.ontu.wdt.layer

data class RequestDto<T>(val message: String, val context: T)
