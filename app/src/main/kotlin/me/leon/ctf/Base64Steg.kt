package me.leon.ctf

import kotlin.math.log2
import me.leon.encode.base.*
import me.leon.ext.binary2Ascii
import me.leon.ext.math.lcm
import me.leon.ext.toBinaryString

/**
 * https://blog.csdn.net/Sanctuary1307/article/details/113836907
 *
 * @author Leon
 * @since 2022-12-06 10:14
 */
fun String.baseStegDecrypt(dict: String = BASE64_DICT) =
    lines()
        .filterNot { it.isBlank() }
        .joinToString("") { it.baseLineSteg(dict) }
        .replace("(?:0{8})*$".toRegex(), "")
        //        .also { println(it) }
        .trimBits()
        //        .also { println(it) }
        .binary2Ascii()

private fun String.trimBits(bitCount: Int = BYTE_BITS) =
    chunked(bitCount).joinToString("") { bits ->
        if (bits.length == bitCount) {
            bits
        } else {
            "".takeIf { bits.all { it == '0' } } ?: bits.padding("0", bitCount)
        }
    }

fun String.baseStegEncrypt(raw: String, dict: String = BASE64_DICT): String {
    val data = toBinaryString()
    val encode =
        raw.lines().map {
            if (it.isBlank()) {
                it
            } else {
                if (dict.length == 64) {
                    it.base64(dict)
                } else {
                    it.base32(dict)
                }
            }
        }

    val bits = log2(dict.length.toDouble()).toInt()
    var fromIndex = 0
    val dataMaxLength = encode.sumOf { bits * (BYTE_BITS - it.count { it == '=' }) % BYTE_BITS }
    require(data.length < dataMaxLength) { "Not enough space to hide data!!!" }
    return encode.joinToString(System.lineSeparator()) { s ->
        val count = s.count { it == '=' }
        if (count == 0 || fromIndex >= data.length) {
            s
        } else {
            val modifiedCharIndex = s.lastIndex - count
            val infoSize = bits * (8 - count) % 8
            val endIndex = data.length.coerceAtMost(fromIndex + infoSize)
            val shiftLeft =
                if ((fromIndex + infoSize) > data.length) fromIndex + infoSize - data.length else 0
            val afterIndex =
                dict.indexOf(s[modifiedCharIndex]) +
                    data.substring(fromIndex, endIndex).toInt(2).shl(shiftLeft)
            fromIndex = endIndex
            s.substring(0, modifiedCharIndex) + dict[afterIndex] + "=".repeat(count)
        }
    }
}

private fun String.baseLineSteg(dict: String = BASE64_DICT): String {
    val bits = log2(dict.length.toDouble()).toInt()
    val chunkSize = (bits.toBigInteger().lcm(8.toBigInteger()) / bits.toBigInteger()).toInt()

    return with(chunked(chunkSize).takeLast(1).single()) {
        val count = count { it == '=' }
        if (count > 0) {
            dict
                .indexOf(this[chunkSize - 1 - count])
                .toString(2)
                .padding("0", bits, false)
                .takeLast(bits * (8 - count) % 8)
        } else {
            ""
        }
    }
}
