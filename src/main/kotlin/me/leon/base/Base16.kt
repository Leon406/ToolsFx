package me.leon.base

import me.leon.ext.toBinaryString

const val BASE16_MAP = "0123456789ABCDEF"
const val BASE16_BLOCK_SIZE = 4
const val BYTE_BITS = 8

fun ByteArray.base16() =
    toBinaryString().chunked(BASE16_BLOCK_SIZE).joinToString("") {
        BASE16_MAP[it.padding("0", BASE16_BLOCK_SIZE).toInt(2)].toString()
    }

fun String.base16() = toByteArray().base16()

fun String.base16Decode() =
    toCharArray()
        .joinToString("") {
            BASE16_MAP.indexOf(it).toString(2).padding("0", BASE16_BLOCK_SIZE, false)
        }
        .chunked(BYTE_BITS)
        .map { it.toInt(2).toByte() }
        .filter { it.toInt() != 0 }
        .toByteArray()

fun String.base16Decode2String() = String(base16Decode())
