package ua.edu.ontu.wdt.configuration

import ua.edu.ontu.wdt.layer.IAsyncConfiguration
import java.util.concurrent.CompletableFuture

class AsyncConfiguration : IAsyncConfiguration {

    override fun runIOOperationAsync(lambdaOperation: () -> Unit) {
        Thread{
            lambdaOperation()
        }.start()
//        CompletableFuture.runAsync {
//            println("Async")
//            lambdaOperation()
//        }
    }

    override fun runAsync(lambdaOperation: () -> Unit) {
        CompletableFuture.runAsync {
            lambdaOperation()
        }
    }
}