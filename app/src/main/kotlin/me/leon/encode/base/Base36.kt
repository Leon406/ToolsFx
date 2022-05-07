package me.leon.encode.base

const val BASE36_RADIX = 36
const val BASE36_DICT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun String.base36(dict: String = BASE36_DICT) =
    radixNEncode(BASE36_RADIX, dict.ifEmpty { BASE36_DICT })

fun ByteArray.base36(dict: String = BASE36_DICT) =
    radixNEncode(BASE36_RADIX, dict.ifEmpty { BASE36_DICT })

fun String.base36Decode(dict: String = BASE36_DICT) =
    radixNDecode(BASE36_RADIX, dict.ifEmpty { BASE36_DICT })

fun String.base36Decode2String(dict: String = BASE36_DICT) =
    radixNDecode2String(BASE36_RADIX, dict.ifEmpty { BASE36_DICT })
