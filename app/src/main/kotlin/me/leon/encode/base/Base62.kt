package me.leon.encode.base

const val BASE62_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun String.base62(dict: String = BASE62_DICT) = radixNEncode(dict.ifEmpty { BASE62_DICT })

fun ByteArray.base62(dict: String = BASE62_DICT) = radixNEncode(dict.ifEmpty { BASE62_DICT })

fun String.base62Decode(dict: String = BASE62_DICT) = radixNDecode(dict.ifEmpty { BASE62_DICT })

fun String.base62Decode2String(dict: String = BASE62_DICT) =
    radixNDecode2String(dict.ifEmpty { BASE62_DICT })
