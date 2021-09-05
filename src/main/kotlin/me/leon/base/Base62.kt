package me.leon.base

const val BASE62_RADIX = 62
const val BASE_62_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun String.base62() = baseNEncode(BASE62_RADIX, BASE_62_DICT)

fun ByteArray.base62() = baseNEncode(BASE62_RADIX, BASE_62_DICT)

fun String.base62Decode() = baseNDecode(BASE62_RADIX, BASE_62_DICT)

fun String.base62Decode2String() = baseNDecode2String(BASE62_RADIX, BASE_62_DICT)
