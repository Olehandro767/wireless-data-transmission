package ua.edu.ontu.wdt.layer.impl.protocol

enum class Protocol {

    TCP;

    companion object {
        fun toEnum(value: String): Protocol {
            for (item in entries) {
                if (item.name.equals(value, true)) {
                    return item
                }
            }

            throw IllegalArgumentException()
        }
    }
}