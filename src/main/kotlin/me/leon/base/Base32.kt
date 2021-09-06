package me.leon.base

import me.leon.ext.toBinaryString

const val BASE32_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
const val BASE32_BLOCK_SIZE = 5

fun ByteArray.base32(dict: String = BASE32_MAP) =
    toBinaryString()
        .chunked(BASE32_BLOCK_SIZE)
        .joinToString("") { dict[it.padding("0", BASE32_BLOCK_SIZE).toInt(2)].toString() }
        .padding("=", BYTE_BITS) // lcm (5,8) /5 = 8

fun String.base32(dict: String = BASE32_MAP) = toByteArray().base32(dict)

fun String.base32Decode(dict: String = BASE32_MAP) =
    toCharArray()
        .filter { it != '=' }
        .joinToString("") { dict.indexOf(it).toString(2).padding("0", BASE32_BLOCK_SIZE, false) }
        .chunked(BYTE_BITS)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()

fun String.base32Decode2String(dict: String = BASE32_MAP) = String(base32Decode(dict))
