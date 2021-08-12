package me.leon.base

import me.leon.ext.toBinaryString

const val BASE16_MAP = "0123456789ABCDEF"

fun ByteArray.base16() =
    toBinaryString().chunked(4).joinToString("") {
        BASE16_MAP[it.padding("0", 4).toInt(2)].toString()
    }

fun String.base16() = toByteArray().base16()

fun String.base16Decode() =
    toCharArray()
        .joinToString("") { BASE16_MAP.indexOf(it).toString(2).padding("0", 4, false) }
        .chunked(8)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()

fun String.base16Decode2String() = String(base16Decode())
