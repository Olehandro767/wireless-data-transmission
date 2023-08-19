package ua.edu.ontu.wdt.layer.utils

import java.net.NetworkInterface.getNetworkInterfaces
import java.util.regex.Pattern

object Ipv4Utils {

    fun getIpsV4ForCurrentDevice(): Array<String> {
        val tempListOfIps = ArrayList<String>()
        val enumerationNetworkInterfaces = getNetworkInterfaces()
        val regexp =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$")

        while (enumerationNetworkInterfaces.hasMoreElements()) {
            val inetAddresses = enumerationNetworkInterfaces.nextElement().inetAddresses

            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()

                if (regexp.matcher(inetAddress.hostAddress).matches() && !inetAddress.hostAddress.startsWith("127")) {
                    tempListOfIps.add(inetAddress.hostAddress)
                }
            }
        }

        return tempListOfIps.toTypedArray()
    }
}