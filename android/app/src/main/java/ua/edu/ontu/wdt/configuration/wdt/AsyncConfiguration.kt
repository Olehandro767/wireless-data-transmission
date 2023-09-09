package ua.edu.ontu.wdt.configuration.wdt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import kotlin.coroutines.CoroutineContext

class AsyncConfiguration(
    private val _ioCoroutineContext: CoroutineContext = Dispatchers.IO,
    private val _coroutineContext: CoroutineContext = Job() + Dispatchers.Main,
) : IAsyncConfiguration {

    private suspend fun ioAsyncFun(lambdaOperation: () -> Unit) {
        withContext(_ioCoroutineContext) {
            lambdaOperation()
        }
    }

    private suspend fun asyncFun(lambdaOperation: () -> Unit) {
        withContext(_coroutineContext) {
            lambdaOperation()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun runIOOperationAsync(lambdaOperation: () -> Unit) {
        CoroutineScope(_ioCoroutineContext).launch {
            ioAsyncFun(lambdaOperation)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun runAsync(lambdaOperation: () -> Unit) {
        CoroutineScope(_coroutineContext).launch {
            asyncFun(lambdaOperation)
        }
    }
}