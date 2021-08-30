package me.leon.base

fun String.base58() = baseNEncode(58)

fun ByteArray.base58() = baseNEncode(58)

fun String.base58Decode() = baseNDecode(58)

fun String.base58Decode2String() = baseNDecode2String(58)
