package me.leon.base

import me.leon.ext.toBinaryString

const val BASE16_MAP = "0123456789ABCDEF"

fun String.base16() =
    toByteArray().toBinaryString().chunked(4).joinToString("") {
        BASE16_MAP[it.padding("0", 4).toInt(2)].toString()
    }

fun String.base16Decode() =
    String(
        toCharArray()
            .joinToString("") { BASE16_MAP.indexOf(it).toString(2).padding("0", 4, false) }
            .chunked(8)
            .map { it.toInt(2).toByte() }
            .filter { it.toInt() != 0 }
            .toByteArray()
    )
