package me.leon.base

const val BASE58_RADIX = 58

fun String.base58() = baseNEncode(BASE58_RADIX)

fun ByteArray.base58() = baseNEncode(BASE58_RADIX)

fun String.base58Decode() = baseNDecode(BASE58_RADIX)

fun String.base58Decode2String() = baseNDecode2String(BASE58_RADIX)
