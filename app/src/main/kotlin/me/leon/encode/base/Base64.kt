package me.leon.encode.base

import java.nio.charset.Charset
import me.leon.ext.stripAllSpace
import me.leon.ext.toBinaryString

const val BASE64_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
const val BASE64_URL_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
const val BASE64_BLOCK_SIZE = 6
const val BASE64_PADDING_SIZE = 4
const val BYTE_MASK = 0xFF

fun String.base64(
    dict: String = BASE64_DICT,
    charset: Charset = Charsets.UTF_8,
    needPadding: Boolean = true
) = toByteArray(charset).base64(dict, needPadding)

fun ByteArray.base64(dict: String = BASE64_DICT, needPadding: Boolean = true) =
    toBinaryString()
        .chunked(BASE64_BLOCK_SIZE)
        .joinToString("") {
            dict.ifEmpty { BASE64_DICT }[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString()
        }
        .run {
            // lcm (6, 8) /6 = 4
            if (needPadding) {
                padding("=".takeUnless { dict.contains(it) } ?: "＝", BASE64_PADDING_SIZE)
            } else {
                this
            }
        }

fun String.base64Decode(dict: String = BASE64_DICT) =
    stripAllSpace() // remove all space  RFC 2045定义，每行为76个字符，行末加入\r\n
        .asIterable()
        .filter { (!dict.contains("=") && it != '=') || dict.contains(it) }
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

fun ByteArray.base64Decode(dict: String = BASE64_DICT) = decodeToString().base64Decode(dict)

fun String.base64Decode2String(dict: String = BASE64_DICT) = base64Decode(dict).decodeToString()

/** 标准的Base64并不适合直接放在URL里传输，因为URL编码器会把标准Base64中的“/”和“+”字符变为形如“%XX”的形式， */
fun String.base64Url(dict: String = BASE64_URL_DICT) = toByteArray().base64Url(dict)

fun ByteArray.base64Url(dict: String = BASE64_URL_DICT) =
    base64(dict.ifEmpty { BASE64_URL_DICT }, false)

fun String.base64UrlDecode(dict: String = BASE64_URL_DICT) =
    base64Decode(dict.ifEmpty { BASE64_URL_DICT })

fun String.base64UrlDecode2String(dict: String = BASE64_URL_DICT) = String(base64UrlDecode(dict))

fun String.padding(char: String, block: Int, isAfter: Boolean = true) =
    chunked(block).joinToString("") {
        it.takeIf { it.length == block }
            ?: if (isAfter) {
                it + char.repeat(block - it.length)
            } else {
                char.repeat(block - it.length) + it
            }
    }
