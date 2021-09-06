package me.leon.base

import me.leon.ext.toBinaryString

const val BASE92_MAP =
    "!#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_abcdefghijklmnopqrstuvwxyz{|}"

const val BASE92_BLOCK_SIZE = 13
const val BASE92_BLOCK_SIZE_HALF = 6

fun String.base92Encode2String(dict: String = BASE92_MAP): String {
    if (isEmpty()) return "~"
    return toByteArray().toBinaryString().chunked(BASE92_BLOCK_SIZE).joinToString("") {
        if (it.length < 7) dict[it.padding("0", BASE92_BLOCK_SIZE_HALF).toInt(2)].toString()
        else
            with(it.padding("0", BASE92_BLOCK_SIZE).toInt(2)) {
                dict[this / 91] + dict[this % 91].toString()
            }
    }
}

fun ByteArray.base92Encode(dict: String = BASE92_MAP) = String(this).base92Encode2String(dict)

fun String.base92Decode(dict: String = BASE92_MAP): ByteArray {
    if (this == "~") return "".toByteArray()
    return toCharArray()
        .toList()
        .chunked(2)
        .joinToString("") {
            if (it.size > 1)
                (dict.indexOf(it.first()) * 91 + dict.indexOf(it[1]))
                    .toString(2)
                    .padding("0", BASE92_BLOCK_SIZE, false)
            else dict.indexOf(it.first()).toString(2)
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()
}

fun String.base92Decode2String(dict: String = BASE92_MAP) = String(base92Decode(dict))
