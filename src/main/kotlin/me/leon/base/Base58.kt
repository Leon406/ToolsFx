package me.leon.base

const val BASE58_RADIX = 58

fun String.base58(dict: String = BASE_58_DICT) = baseNEncode(BASE58_RADIX, dict)

fun ByteArray.base58(dict: String = BASE_58_DICT) = baseNEncode(BASE58_RADIX, dict)

fun String.base58Decode(dict: String = BASE_58_DICT) = baseNDecode(BASE58_RADIX, dict)

fun String.base58Decode2String(dict: String = BASE_58_DICT) = baseNDecode2String(BASE58_RADIX, dict)
