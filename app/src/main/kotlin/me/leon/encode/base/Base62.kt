package me.leon.encode.base

const val BASE62_RADIX = 62
const val BASE62_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun String.base62(dict: String = BASE62_DICT) =
    radixNEncode(BASE62_RADIX, dict.ifEmpty { BASE62_DICT })

fun ByteArray.base62(dict: String = BASE62_DICT) =
    radixNEncode(BASE62_RADIX, dict.ifEmpty { BASE62_DICT })

fun String.base62Decode(dict: String = BASE62_DICT) =
    radixNDecode(BASE62_RADIX, dict.ifEmpty { BASE62_DICT })

fun String.base62Decode2String(dict: String = BASE62_DICT) =
    radixNDecode2String(BASE62_RADIX, dict.ifEmpty { BASE62_DICT })
