package me.leon

import me.leon.encode.base.BYTE_BITS
import me.leon.ext.toBinaryString

/** https://en.wikipedia.org/wiki/Xxencoding */
const val XX_DICT = "+-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
const val BASE64_BLOCK_SIZE = 6
const val BYTE_MASK = 0xFF

fun String.xxEncode(dict: String = XX_DICT) = toByteArray().xxEncode(dict)

fun ByteArray.xxEncode(dict: String = XX_DICT) =
    toBinaryString().chunked(8 * 45).joinToString("") {
        val leadChar = XX_DICT[(it.length / 8)]
        "$leadChar" +
            it.chunked(BASE64_BLOCK_SIZE).joinToString("") {
                dict.ifEmpty { XX_DICT }[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString()
            } +
            if (leadChar == 'h') System.lineSeparator() else ""
    }

fun String.xxDecode(dict: String = XX_DICT) =
    this.filterNot { it == '\r' || it == '\n' }
        .chunked(61)
        .joinToString("") {
            it.substring(1, XX_DICT.indexOf(it.first()) * 4 / 3 + 1)
                .map {
                    dict
                        .ifEmpty { XX_DICT }
                        .indexOf(it)
                        .toString(2)
                        .padding("0", BASE64_BLOCK_SIZE, false)
                }
                .joinToString("")
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()

fun String.xxDecode2String(dict: String = XX_DICT) = String(xxDecode(dict))

fun String.padding(char: String, block: Int, isAfter: Boolean = true) =
    chunked(block).joinToString("") {
        it.takeIf { it.length == block }
            ?: if (isAfter) it + char.repeat(block - it.length)
            else char.repeat(block - it.length) + it
    }
