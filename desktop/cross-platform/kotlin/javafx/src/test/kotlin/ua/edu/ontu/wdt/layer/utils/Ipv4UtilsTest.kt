package ua.edu.ontu.wdt.layer.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ua.edu.ontu.wdt.layer.types.ip.Ipv4Set
import java.util.*

class Ipv4UtilsTest {

    @Test
    fun getIpsV4ForCurrentDevice() {
        val ips = Ipv4Utils.getIpsV4ForCurrentDevice()
        println(ips.contentToString())

        if (ips.isNotEmpty()) {
            assertTrue(Arrays.stream(ips).noneMatch { it.equals("127.0.0.1") || it.equals("localhost") })
        }
    }

    @Test
    fun iterableIpv4Test() {
        var index = 0
        for (ip in Ipv4Set("127.0.0.1")) {
            println(ip)
            val localIndex = ip.substring(ip.lastIndexOf('.') + 1).toInt()
            if (index == 1) {
                index++
            }
            assertEquals(index++, localIndex)
        }
    }
}