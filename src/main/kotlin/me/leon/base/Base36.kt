package me.leon.base

const val BASE36_RADIX = 36
const val BASE_36_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun String.base36() = baseNEncode(BASE36_RADIX, BASE_36_DICT)

fun ByteArray.base36() = baseNEncode(BASE36_RADIX, BASE_36_DICT)

fun String.base36Decode() = baseNDecode(BASE36_RADIX, BASE_36_DICT)

fun String.base36Decode2String() = baseNDecode2String(BASE36_RADIX, BASE_36_DICT)
