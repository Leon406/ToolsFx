package me.leon.base

import me.leon.ext.toBinaryString

const val BASE32_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

fun ByteArray.base32() =
    toBinaryString()
        .chunked(5)
        .joinToString("") { BASE32_MAP[it.padding("0", 5).toInt(2)].toString() }
        .padding("=", 8) // lcm (5,8) /5 = 8

fun String.base32() = toByteArray().base32()

fun String.base32Decode() =
    toCharArray()
        .filter { it != '=' }
        .joinToString("") { BASE32_MAP.indexOf(it).toString(2).padding("0", 5, false) }
        .chunked(8)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()

fun String.base32Decode2String() = String(base32Decode())
