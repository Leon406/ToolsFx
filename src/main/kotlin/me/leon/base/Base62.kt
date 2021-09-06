package me.leon.base

const val BASE62_RADIX = 62
const val BASE_62_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun String.base62(dict: String = BASE_62_DICT) = baseNEncode(BASE62_RADIX, dict)

fun ByteArray.base62(dict: String = BASE_62_DICT) = baseNEncode(BASE62_RADIX, dict)

fun String.base62Decode(dict: String = BASE_62_DICT) = baseNDecode(BASE62_RADIX, dict)

fun String.base62Decode2String(dict: String = BASE_62_DICT) = baseNDecode2String(BASE62_RADIX, dict)
