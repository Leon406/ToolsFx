package me.leon.encode.base

import java.nio.charset.Charset
import me.leon.UTF8
import me.leon.ext.charsetChange
import me.leon.ext.toBinaryString

const val BASE92_DICT =
    "!#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_abcdefghijklmnopqrstuvwxyz{|}"

const val BASE92_BLOCK_SIZE = 13
const val BASE92_BLOCK_SIZE_HALF = 6

fun String.base92Encode2String(dict: String = BASE92_DICT, charset: String = UTF8): String {
    if (isEmpty()) return "~"
    val dic = dict.ifEmpty { BASE92_DICT }
    return toByteArray(Charset.forName(charset))
        .toBinaryString()
        .chunked(BASE92_BLOCK_SIZE)
        .joinToString("") {
            if (it.length < 7) {
                dic[it.padding("0", BASE92_BLOCK_SIZE_HALF).toInt(2)].toString()
            } else {
                with(it.padding("0", BASE92_BLOCK_SIZE).toInt(2)) {
                    dic[this / 91] + dic[this % 91].toString()
                }
            }
        }
}

fun ByteArray.base92Encode(dict: String = BASE92_DICT, charset: String = "UTF-8") =
    toString(Charset.forName(charset)).base92Encode2String(dict)

fun String.base92Decode(dict: String = BASE92_DICT, charset: String = "UTF-8"): ByteArray {
    if (this == "~") return "".toByteArray()
    val dic = dict.ifEmpty { BASE92_DICT }
    return asIterable()
        .chunked(2)
        .joinToString("") {
            if (it.size > 1) {
                (dic.indexOf(it.first()) * 91 + dic.indexOf(it[1]))
                    .toString(2)
                    .padding("0", BASE92_BLOCK_SIZE, false)
            } else {
                dic.indexOf(it.first()).toString(2)
            }
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()
        .charsetChange("UTF-8", charset)
}

fun String.base92Decode2String(dict: String = BASE92_DICT, charset: String = "UTF-8") =
    base92Decode(dict, charset).toString(Charset.forName(charset))
