package me.leon.ext

import java.nio.charset.Charset
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.encode.octal
import me.leon.encode.octalDecode

val ENCODERS = listOf("raw", "hex", "base64", "oct", "binary")

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

fun ByteArray.encodeTo(encoder: String, charset: String = "UTF-8") =
    when (encoder) {
        "raw" -> toString(Charset.forName(charset))
        "base64" -> base64()
        "hex" -> toHex()
        "oct" -> octal()
        "binary" -> toBinaryString()
        else -> error("Unknown encoder: $encoder")
    }
