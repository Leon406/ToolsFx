package me.leon.encode

import java.security.SecureRandom
import me.leon.UTF8
import me.leon.ext.*

fun String.mixEncode(charset: String = UTF8) =
    toByteArray(charset.toCharset()).joinToString("") { it.mixCharEncode() }

fun ByteArray.mixEncode() = joinToString("") { it.mixCharEncode() }

val chars = charArrayOf('b', 'o', 'x', 'B', 'O', 'X')

fun String.mixDecode(): ByteArray {
    if (first() != '0') return byteArrayOf()
    require(length <= 1024 * 1024) { "data too large" }

    val list = mutableListOf<String>()
    var start = 0
    for (index in indices) {
        if (index < length - 2 && this[index + 1] == '0' && this[index + 2] in chars) {
            list.add(this.substring(start, index + 1))
            start = index + 1
        }
    }
    if (start < length) {
        list.add(substring(start))
    }
    return list.map { it.mixCharDecode() }.toByteArray()
}

fun String.mixDecode2String() = mixDecode().toString(Charsets.UTF_8)

private fun String.mixCharDecode(): Byte {
    val raw = substring(2)
    return when (substring(0, 2)) {
        "0b",
        "0B" -> raw.binary2ByteArray().first()
        "0x",
        "0X" -> raw.hex2ByteArray().first()
        "0o",
        "0O" -> raw.octalByteDecode()
        else -> this.toByteArray().first()
    }
}

val random = SecureRandom()

private fun Byte.mixCharEncode(): String {
    val bytes = byteArrayOf(this)
    return when (random.nextInt(3)) {
        0 -> "0x${bytes.toHex()}"
        1 -> "0b${bytes.toBinaryString(false)}"
        2 -> "0o${octal()}"
        else -> error("never reached")
    }
}
