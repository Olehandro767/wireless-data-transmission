package ua.edu.ontu.wdt.layer.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ArrayUtilsTest {

    @Test
    fun test() {
        val array = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val result1 = ArrayUtils.splitArrayWithConsideration(array, 1)
        val result2 = ArrayUtils.splitArrayWithConsideration(array, 2)
        val result3 = ArrayUtils.splitArrayWithConsideration(array, 4)
        assertEquals(1, result1.size)
        assertEquals(10, result1[0].size)
        assertEquals(2, result2.size)
        assertEquals(5, result2[0].size)
        assertEquals(4, result3.size)
    }
}