package me.leon.ext

import java.math.BigInteger

/** 16进制编解码 */
fun ByteArray.toHex() = String.format("%02x", BigInteger(1, this))

fun String.hex2Ascii() = String(hex2ByteArray(), Charsets.UTF_8)

fun String.hex2ByteArray() =
    toCharArray().toList().chunked(2).map { it.joinToString("").toInt(16).toByte() }.toByteArray()

fun ByteArray.toBinaryString() =
    joinToString("") {
        with((it.toInt() and 0xff).toString(2)) {
            this.takeIf { it.length == 8 } ?: ("0".repeat(8 - this.length) + this)
        }
    }

/** 二进制编解码 */
fun String.toBinaryString() = toByteArray().toBinaryString()

fun String.binary2Ascii() = String(binary2ByteArray(), Charsets.UTF_8)

fun String.binary2ByteArray() =
    toCharArray().toList().chunked(8).map { it.joinToString("").toInt(2).toByte() }.toByteArray()

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
                    if (it.groupValues[0].contains("x", true)) 16 else 10
            }
            .fold(StringBuilder()) { acc, (c, radix) ->
                acc.apply { append(c.toInt(radix).toChar()) }
            }
            .toString()
    else
        split("\\u")
            .filterIndexed { index, _ -> index != 0 }
            .fold(StringBuilder()) { acc, c -> acc.apply { append(c.toInt(16).toChar()) } }
            .toString()
