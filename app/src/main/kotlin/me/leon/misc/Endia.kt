package me.leon.misc

import me.leon.ext.crypto.HEX_LEAD_REGEX
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex

/**
 * @author Leon
 * @since 2023-08-23 11:18
 * @email deadogone@gmail.com
 */
const val HEX_PREFIX = "0x"

enum class Endia : Transfer {

    BIT_REVERSE {
        override fun convert(s: String) =
            HEX_PREFIX +
                if (s.contains(HEX_LEAD_REGEX)) {
                    s.hex2ByteArray().reversedArray().toHex()
                } else {
                    s.toByteArray().reversedArray().toHex()
                }
    },
    LE_BE_16BIT {
        override fun convert(s: String) =
            HEX_PREFIX +
                if (s.contains(HEX_LEAD_REGEX)) {
                    s.replace(HEX_LEAD_REGEX, "").chunked(4).joinToString("") {
                        it.substring(2, 4) + it.substring(0, 2)
                    }
                } else {
                    s.toByteArray(Charsets.UTF_16LE).toHex()
                }
    },
    LE_BE_32BIT {
        override fun convert(s: String) =
            HEX_PREFIX +
                if (s.contains(HEX_LEAD_REGEX)) {
                    s.replace(HEX_LEAD_REGEX, "").chunked(8).joinToString("") {
                        it.hex2ByteArray().reversedArray().toHex()
                    }
                } else {
                    s.toByteArray(Charsets.UTF_32LE).toHex()
                }
    },
    BIT {
        override fun convert(s: String) =
            HEX_PREFIX +
                if (s.contains(HEX_LEAD_REGEX)) {
                    s.replace(HEX_LEAD_REGEX, "")
                } else {
                    s.toByteArray().toHex()
                }
    },
    BE_16BIT {
        override fun convert(s: String) =
            HEX_PREFIX +
                if (s.contains(HEX_LEAD_REGEX)) {
                    s.replace(HEX_LEAD_REGEX, "")
                } else {
                    s.toByteArray(Charsets.UTF_16BE).toHex()
                }
    },
    BE_32BIT {
        override fun convert(s: String) =
            HEX_PREFIX +
                if (s.contains(HEX_LEAD_REGEX)) {
                    s.replace(HEX_LEAD_REGEX, "")
                } else {
                    s.toByteArray(Charsets.UTF_32BE).toHex()
                }
    },
}

interface Transfer {
    fun convert(s: String): String
}
