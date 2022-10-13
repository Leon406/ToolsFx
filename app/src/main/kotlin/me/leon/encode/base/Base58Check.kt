package me.leon.encode.base

fun String.base58Check(dict: String = BASE58_DICT) =
    toByteArray().baseCheck(dict.ifEmpty { BASE58_DICT })

fun ByteArray.base58Check(dict: String = BASE58_DICT) = baseCheck(dict.ifEmpty { BASE58_DICT })

fun String.base58CheckDecode(dict: String = BASE58_DICT) =
    baseCheckDecode(dict.ifEmpty { BASE58_DICT })

fun String.base58CheckDecode2String(dict: String = BASE58_DICT) =
    baseCheckDecode2String(dict.ifEmpty { BASE58_DICT })
