package me.leon.base

import me.leon.ext.toBinaryString

val map32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

fun String.base32() =
    toByteArray()
        .toBinaryString()
        .chunked(5)
        .joinToString("") { map32[it.padding("0", 5).toInt(2)].toString() }
        .padding("=", 8) // lcm (5,8) /5 = 8

fun String.base32Decode() =
    String(
        toCharArray()
            .filter { it != '=' }
            .joinToString("") { map32.indexOf(it).toString(2).padding("0", 5, false) }
            .chunked(8)
            .map { it.toInt(2).toByte() }
            .filter { it.toInt() != 0 }
            .toByteArray()
    )
