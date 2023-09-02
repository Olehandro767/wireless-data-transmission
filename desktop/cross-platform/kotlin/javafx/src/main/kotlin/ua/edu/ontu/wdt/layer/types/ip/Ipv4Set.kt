package ua.edu.ontu.wdt.layer.types.ip

class Ipv4Set(
    ip: String
) : Iterable<String>, Iterator<String>, IIpSetInfo {

    private val ipPrefix = ip.substring(0, ip.lastIndexOf('.'))
    private val currentSuffix = ip.substring(ip.lastIndexOf('.') + 1).toInt()
    private var index = 0

    override val limit = 255

    override fun iterator(): Iterator<String> = this

    override fun hasNext(): Boolean = this.index <= this.limit

    override fun next(): String {
        if (this.index == this.currentSuffix) {
            this.index++
        }

        return "${this.ipPrefix}.${this.index++}"
    }

    fun toList(): List<String> {
        val result = ArrayList<String>(254)
        for (ip in this) {
            result.add(ip)
        }
        this.index = 0
        return result
    }

    fun toArray(): Array<String> = this.toList().toTypedArray()
}

