package me.leon.encode.base

import me.leon.ext.toBinaryString

const val BASE16_DICT = "0123456789ABCDEF"
const val BASE16_BLOCK_SIZE = 4
const val BYTE_BITS = 8

fun ByteArray.base16(dict: String = BASE16_DICT) =
    toBinaryString().chunked(BASE16_BLOCK_SIZE).joinToString("") {
        dict.ifEmpty { BASE16_DICT }[it.padding("0", BASE16_BLOCK_SIZE).toInt(2)].toString()
    }

fun String.base16Decode(dict: String = BASE16_DICT) =
    asIterable()
        .joinToString("") {
            dict
                .ifEmpty { BASE16_DICT }
                .indexOf(it)
                .toString(2)
                .padding("0", BASE16_BLOCK_SIZE, false)
        }
        .chunked(BYTE_BITS)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()
