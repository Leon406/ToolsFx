package me.leon.encode.base

fun String.base58(dict: String = BASE58_DICT) = radixNEncode(dict.ifEmpty { BASE58_DICT })

fun ByteArray.base58(dict: String = BASE58_DICT) = radixNEncode(dict.ifEmpty { BASE58_DICT })

fun String.base58Decode(dict: String = BASE58_DICT) = radixNDecode(dict.ifEmpty { BASE58_DICT })
