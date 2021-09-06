package me.leon

import me.leon.base.BYTE_BITS
import me.leon.base.BYTE_MASK
import me.leon.base.padding
import me.leon.ext.toBinaryString

const val BASE92_MAP =
    "!#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_abcdefghijklmnopqrstuvwxyz{|}"

const val BASE92_BLOCK_SIZE = 13
const val BASE92_BLOCK_SIZE_HALF = 6

fun String.base92Encode2String(): String {
    if (isEmpty()) return "~"
    return toByteArray().toBinaryString().chunked(BASE92_BLOCK_SIZE).joinToString("") {
        if (it.length < 7) BASE92_MAP[it.padding("0", BASE92_BLOCK_SIZE_HALF).toInt(2)].toString()
        else
            with(it.padding("0", BASE92_BLOCK_SIZE).toInt(2)) {
                BASE92_MAP[this / 91] + BASE92_MAP[this % 91].toString()
            }
    }
}

fun ByteArray.base92Encode() = String(this).base92Encode2String()

fun String.base92Decode(): ByteArray {
    if (this == "~") return "".toByteArray()
    return toCharArray()
        .toList()
        .chunked(2)
        .joinToString("") {
            if (it.size > 1)
                (BASE92_MAP.indexOf(it.first()) * 91 + BASE92_MAP.indexOf(it[1]))
                    .toString(2)
                    .padding("0", BASE92_BLOCK_SIZE, false)
            else BASE92_MAP.indexOf(it.first()).toString(2)
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()
}

fun String.base92Decode2String() = String(base92Decode())
