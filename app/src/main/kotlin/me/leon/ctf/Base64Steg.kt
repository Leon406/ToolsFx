package me.leon.ctf

import me.leon.encode.base.*
import me.leon.ext.binary2Ascii
import me.leon.ext.toBinaryString

/**
 * https://blog.csdn.net/Sanctuary1307/article/details/113836907
 * @author Leon
 * @since 2022-12-06 10:14
 * @email: deadogone@gmail.com
 */
fun String.base64StegDecrypt() =
    lines()
        .filterNot { it.isBlank() }
        .joinToString("") { it.base64LineSteg() }
        .replace("(?:0{8})*$".toRegex(), "")
        .propBinaryString()
        .binary2Ascii()

fun String.base64StegEncrypt(raw: String): String {
    val data = toBinaryString()
    val encode =
        raw.lines().map {
            if (it.isNotBlank()) {
                it.base64()
            } else {
                it
            }
        }
    var fromIndex = 0
    val eqNumbers = encode.joinToString("").count { it == '=' }
    require(data.length < eqNumbers * 2) { "Not enough space to hide data!!!" }
    return encode.joinToString(System.lineSeparator()) { s ->
        val count = s.count { it == '=' }
        if (count == 0 || fromIndex >= data.length) {
            s
        } else {
            val modifiedCharIndex = s.lastIndex - count
            val endIndex = data.length.coerceAtMost(fromIndex + count * 2)
            val afterIndex =
                BASE64_DICT.indexOf(s[modifiedCharIndex]) +
                    data.substring(fromIndex, endIndex).toInt(2)
            fromIndex = endIndex
            s.substring(0, modifiedCharIndex) + BASE64_DICT[afterIndex] + "=".repeat(count)
        }
    }
}

private fun String.propBinaryString() =
    when (length % 8) {
        0 -> this
        2 ->
            if (endsWith("00")) {
                substring(0, length - 2)
            } else {
                replace("00(?:(\\d\\d)?|(\\d{4}))$".toRegex(), "$1$2")
            }
        4 -> replace("0000(\\d\\d)?$".toRegex(), "$1")
        6 -> replace("000000(\\d\\d)?$".toRegex(), "$1")
        else -> error("")
    }

private fun String.base64LineSteg() =
    with(chunked(4).takeLast(1).single()) {
        val count = count { it == '=' }
        if (count > 0) {
            BASE64_DICT.indexOf(this[3 - count])
                .toString(2)
                .padding("0", 6, false)
                .takeLast(count * 2)
        } else {
            ""
        }
    }
