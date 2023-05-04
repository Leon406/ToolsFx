package me.leon.ext

import java.nio.charset.Charset
import me.leon.encode.*
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.crypto.BINARY_REGEX
import me.leon.ext.crypto.HEX_WITH_LEAD_REGEX

/** note: 不兼容编码转换会导致数据丢失,需要两个编码都能表示才能正常工作 */
fun ByteArray.charsetChange(from: String, to: String) =
    toString(Charset.forName(from)).toByteArray(Charset.forName(to))

fun ByteArray.padStart(length: Int, byte: Byte): ByteArray {
    if (size % length == 0) return this
    val padCount = length - size % length
    val bytes = ByteArray(size + padCount)
    repeat(padCount) { bytes[it] = byte }
    copyInto(bytes, padCount)
    return bytes
}

fun String.decodeToByteArray(encoder: String = "raw", charset: String = "UTF-8") =
    when (encoder) {
        "raw" -> toByteArray(Charset.forName(charset))
        "oct" -> octalDecode()
        "binary" -> binary2ByteArray()
        "hex" -> hex2ByteArray()
        "base64" -> base64Decode()
        else -> error("Unknown encoder: $encoder")
    }

/**
 * 自动解码成 ByteArray
 *
 * 0b/0B, 按照二进制解码 0x/0X, 按照十六进制解码
 */
fun String.autoDecodeToByteArray(isDecode: Boolean = false): ByteArray =
    if (length < 3) {
        toByteArray()
    } else {
        when {
            BINARY_REGEX.matches(this) -> binary2ByteArray()
            isDecode || HEX_WITH_LEAD_REGEX.matches(this) -> hex2ByteArray()
            else -> toByteArray()
        }
    }

fun ByteArray.encodeTo(encoder: String, charset: String = "UTF-8") =
    when (encoder) {
        "raw" -> toString(Charset.forName(charset))
        "base64" -> base64()
        "hex" -> toHex()
        "oct" -> octal()
        "binary" -> toBinaryString()
        else -> error("Unknown encoder: $encoder")
    }

/**
 * UTF_32LE --> UTF_32BE 再转为 int
 *
 * 作用和python的stack.unpack('<1',bytes([1,2,3,4]))方法一致
 */
fun ByteArray.unpack(): Int {
    require(size == 4)
    reverse()
    return fold(0) { acc, byte -> acc shl 8 or (byte.toInt() and 0xff) }
}

fun Int.pack(size: Int = 4): ByteArray {
    val bytes = ByteArray(size)
    for (index in bytes.indices) {
        bytes[index] = ((this shr 8 * (bytes.lastIndex - index)) and 0xff).toByte()
    }
    return bytes
}

fun ByteArray.setByteArray(start: Int, bytes: ByteArray) {
    val end = (start + bytes.size).coerceAtMost(size)
    for (i in start until end) {
        this[i] = bytes[i - start]
    }
}
