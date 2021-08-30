package me.leon.base

fun String.base58Check() = toByteArray().baseCheck(58)

fun ByteArray.base58Check() = baseCheck(58)

fun String.base58CheckDecode() = baseCheckDecode(58)

fun String.base58CheckDecode2String() = baseCheckDecode2String(58)
