package me.leon.base

import me.leon.ext.toBinaryString

const val Base32Map = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

fun String.base32() =
    toByteArray()
        .toBinaryString()
        .chunked(5)
        .joinToString("") { Base32Map[it.padding("0", 5).toInt(2)].toString() }
        .padding("=", 8) // lcm (5,8) /5 = 8

fun String.base32Decode() =
    String(
        toCharArray()
            .filter { it != '=' }
            .joinToString("") { Base32Map.indexOf(it).toString(2).padding("0", 5, false) }
            .chunked(8)
            .map { it.toInt(2).toByte() }
            .filter { it.toInt() != 0 }
            .toByteArray()
    )
