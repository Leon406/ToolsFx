package me.leon.base

fun String.base58Check() = toByteArray().baseCheck(BASE58_RADIX)

fun ByteArray.base58Check() = baseCheck(BASE58_RADIX)

fun String.base58CheckDecode() = baseCheckDecode(BASE58_RADIX)

fun String.base58CheckDecode2String() = baseCheckDecode2String(BASE58_RADIX)
