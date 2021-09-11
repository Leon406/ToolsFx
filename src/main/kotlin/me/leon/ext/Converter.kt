package me.leon.ext

import me.leon.encode.base.BYTE_BITS
import me.leon.encode.base.BYTE_MASK
import tornadofx.*

const val HEX_RADIX = 16
const val DECIMAL_RADIX = 10

/** 16进制编解码 */
fun ByteArray.toHex() = hex

fun String.hex2Ascii() = String(hex2ByteArray(), Charsets.UTF_8)

fun String.hex2ByteArray() =
    toList().chunked(2).map { it.joinToString("").toInt(HEX_RADIX).toByte() }.toByteArray()

fun ByteArray.toBinaryString() =
    joinToString("") {
        with((it.toInt() and BYTE_MASK).toString(2)) {
            this.takeIf { it.length == BYTE_BITS } ?: ("0".repeat(BYTE_BITS - this.length) + this)
        }
    }

/** 二进制编解码 */
fun String.toBinaryString() = toByteArray().toBinaryString()

fun String.binary2Ascii() = String(binary2ByteArray(), Charsets.UTF_8)

fun String.binary2ByteArray() =
    toList().chunked(BYTE_BITS).map { it.joinToString("").toInt(2).toByte() }.toByteArray()

/** unicode编解码 */
fun String.toUnicodeString() =
    fold(StringBuilder()) { acc, c ->
            acc.apply {
                append("\\u")
                append(Integer.toHexString(c.code))
            }
        }
        .toString()

fun String.unicode2String() =
    if (contains("&#", true))
        "(?i)&#x([0-9a-f]+);|&#(\\d+);"
            .toRegex()
            .findAll(this)
            .map {
                it.groupValues[1].ifEmpty { it.groupValues[2] } to
                    if (it.groupValues[0].contains("x", true)) HEX_RADIX else DECIMAL_RADIX
            }
            .fold(StringBuilder()) { acc, (c, radix) ->
                acc.apply { append(c.toInt(radix).toChar()) }
            }
            .toString()
    else
        split("\\\\u\\+?".toRegex())
            .filterIndexed { index, _ -> index != 0 }
            .fold(StringBuilder()) { acc, c -> acc.apply { append(c.toInt(HEX_RADIX).toChar()) } }
            .toString()
