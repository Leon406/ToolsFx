package me.leon.base

const val BASE58_RADIX = 58

fun String.base58(dict: String = BASE58_DICT) =
    baseNEncode(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })

fun ByteArray.base58(dict: String = BASE58_DICT) =
    baseNEncode(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })

fun String.base58Decode(dict: String = BASE58_DICT) =
    baseNDecode(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })

fun String.base58Decode2String(dict: String = BASE58_DICT) =
    baseNDecode2String(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })
