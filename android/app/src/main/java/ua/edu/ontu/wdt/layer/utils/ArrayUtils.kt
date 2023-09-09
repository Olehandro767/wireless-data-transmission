package ua.edu.ontu.wdt.layer.utils

import kotlin.math.ceil

object ArrayUtils {

    fun <T> splitArrayWithConsideration(array: List<T>, maxNumberOfThread: Int): List<List<T>> {
        var index = 0
        val chunkSize = ceil(array.size.toDouble() / maxNumberOfThread.toDouble()).toInt()
        val result = ArrayList<ArrayList<T>>()

        while (index < array.size) {
            val chunk = ArrayList<T>()

            for (ignore in 0 until chunkSize) {
                if (index < array.size) {
                    chunk.add(array[index++])
                }
            }

            result.add(chunk)
        }

        return result
    }

    inline fun <reified T> splitArrayWithConsideration(
        array: Array<T>,
        maxNumberOfThread: Int
    ): List<Array<T>> {
        return splitArrayWithConsideration(
            listOf(*array),
            maxNumberOfThread
        ).map { it.toTypedArray() }.toList()
    }
}