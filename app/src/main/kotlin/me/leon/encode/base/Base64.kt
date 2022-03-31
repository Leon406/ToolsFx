package me.leon.encode.base

import java.nio.charset.Charset
import me.leon.ext.stripAllSpace
import me.leon.ext.toBinaryString

const val BASE64_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
const val BASE64_URL_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
const val BASE64_BLOCK_SIZE = 6
const val BASE64_PADDING_SIZE = 4
const val BYTE_MASK = 0xFF

fun String.base64(dict: String = BASE64_DICT, charset: Charset = Charsets.UTF_8) =
    toByteArray(charset).base64(dict)

fun ByteArray.base64(dict: String = BASE64_DICT, needPadding: Boolean = true) =
    toBinaryString()
        .chunked(BASE64_BLOCK_SIZE)
        .joinToString("") {
            dict.ifEmpty { BASE64_DICT }[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString()
        }
        .run {
            if (needPadding) padding("=", BASE64_PADDING_SIZE) // lcm (6, 8) /6 = 4
            else this
        }

fun String.base64Decode(dict: String = BASE64_DICT) =
    stripAllSpace()
        .toCharArray()
        .filter { it != '=' }
        .joinToString("") {
            dict
                .ifEmpty { BASE64_DICT }
                .indexOf(it)
                .toString(2)
                .padding("0", BASE64_BLOCK_SIZE, false)
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()

fun String.base64Decode2String(dict: String = BASE64_DICT) = String(base64Decode(dict))

/** 标准的Base64并不适合直接放在URL里传输，因为URL编码器会把标准Base64中的“/”和“+”字符变为形如“%XX”的形式， */
fun String.safeBase64(dict: String = BASE64_URL_DICT) = toByteArray().safeBase64(dict)

fun ByteArray.safeBase64(dict: String = BASE64_URL_DICT) = base64(dict.ifEmpty { BASE64_URL_DICT })

fun String.safeBase64Decode(dict: String = BASE64_URL_DICT) =
    base64Decode(dict.ifEmpty { BASE64_URL_DICT })

fun String.safeBase64Decode2String(dict: String = BASE64_URL_DICT) = String(safeBase64Decode(dict))

fun String.padding(char: String, block: Int, isAfter: Boolean = true) =
    chunked(block).joinToString("") {
        it.takeIf { it.length == block }
            ?: if (isAfter) it + char.repeat(block - it.length)
            else char.repeat(block - it.length) + it
    }
