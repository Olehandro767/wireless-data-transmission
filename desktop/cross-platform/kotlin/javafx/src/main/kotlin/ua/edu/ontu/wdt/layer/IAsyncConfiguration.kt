package ua.edu.ontu.wdt.layer

interface IAsyncConfiguration {

    fun runIOOperationAsync(lambdaOperation: () -> Unit)

    fun runAsync(lambdaOperation: () -> Unit)
}