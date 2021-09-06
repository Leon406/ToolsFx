package me.leon.base

fun String.base58Check(dict: String = BASE_58_DICT) = toByteArray().baseCheck(BASE58_RADIX, dict)

fun ByteArray.base58Check(dict: String = BASE_58_DICT) = baseCheck(BASE58_RADIX, dict)

fun String.base58CheckDecode(dict: String = BASE_58_DICT) = baseCheckDecode(BASE58_RADIX, dict)

fun String.base58CheckDecode2String(dict: String = BASE_58_DICT) =
    baseCheckDecode2String(BASE58_RADIX, dict)
