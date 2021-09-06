package me.leon.base

const val BASE36_RADIX = 36
const val BASE_36_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun String.base36(dict: String = BASE_36_DICT) = baseNEncode(BASE36_RADIX, dict)

fun ByteArray.base36(dict: String = BASE_36_DICT) = baseNEncode(BASE36_RADIX, dict)

fun String.base36Decode(dict: String = BASE_36_DICT) = baseNDecode(BASE36_RADIX, dict)

fun String.base36Decode2String(dict: String = BASE_36_DICT) = baseNDecode2String(BASE36_RADIX, dict)
