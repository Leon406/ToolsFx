package me.leon.base

import me.leon.ext.toBinaryString

const val BASE32_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
const val BASE32_BLOCK_SIZE = 5

fun ByteArray.base32() =
    toBinaryString()
        .chunked(BASE32_BLOCK_SIZE)
        .joinToString("") { BASE32_MAP[it.padding("0", BASE32_BLOCK_SIZE).toInt(2)].toString() }
        .padding("=", BYTE_BITS) // lcm (5,8) /5 = 8

fun String.base32() = toByteArray().base32()

fun String.base32Decode() =
    toCharArray()
        .filter { it != '=' }
        .joinToString("") {
            BASE32_MAP.indexOf(it).toString(2).padding("0", BASE32_BLOCK_SIZE, false)
        }
        .chunked(BYTE_BITS)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()

fun String.base32Decode2String() = String(base32Decode())
