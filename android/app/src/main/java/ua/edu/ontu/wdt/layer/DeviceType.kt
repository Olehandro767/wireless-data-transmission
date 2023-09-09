package ua.edu.ontu.wdt.layer

enum class DeviceType {
    PC, MOBILE, undefined; // FIXME

    companion object {
        fun findType(type: String): DeviceType {
            for (item in entries) {
                if (item.name == type.uppercase()) {
                    return item
                }
            }

            return undefined
        }
    }
}