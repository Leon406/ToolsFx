package me.leon.base

import me.leon.ext.toBinaryString


val map16 = "0123456789ABCDEF"
fun String.base16() =
    toByteArray().toBinaryString()
        .chunked(4)
        .joinToString("") { map16[it.padding("0", 4).toInt(2)].toString() }


fun String.base16Decode() =
    String(
        toCharArray()
            .joinToString("") { map16.indexOf(it).toString(2).padding("0", 4, false) }
            .chunked(8)
            .map { it.toInt(2).toByte() }
            .filter { it.toInt() != 0 }
            .toByteArray()
    )



