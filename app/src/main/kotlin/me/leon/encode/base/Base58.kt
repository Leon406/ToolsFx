package me.leon.encode.base

const val BASE58_RADIX = 58

fun String.base58(dict: String = BASE58_DICT) =
    radixNEncode(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })

fun ByteArray.base58(dict: String = BASE58_DICT) =
    radixNEncode(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })

fun String.base58Decode(dict: String = BASE58_DICT) =
    radixNDecode(BASE58_RADIX, dict.ifEmpty { BASE58_DICT })
