package me.leon.encode.base

import me.leon.ext.toBinaryString

const val BASE32_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
const val BASE32_BLOCK_SIZE = 5

fun ByteArray.base32(dict: String = BASE32_DICT) =
    toBinaryString()
        .chunked(BASE32_BLOCK_SIZE)
        .joinToString("") {
            dict.ifEmpty { BASE32_DICT }[it.padding("0", BASE32_BLOCK_SIZE).toInt(2)].toString()
        }
        .padding("=", BYTE_BITS) // lcm (5,8) /5 = 8

fun String.base32(dict: String = BASE32_DICT) = toByteArray().base32(dict)

fun String.base32Decode(dict: String = BASE32_DICT) =
    asIterable()
        .filter { it != '=' }
        .joinToString("") {
            dict
                .ifEmpty { BASE32_DICT }
                .indexOf(it)
                .toString(2)
                .padding("0", BASE32_BLOCK_SIZE, false)
        }
        .chunked(BYTE_BITS)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()
