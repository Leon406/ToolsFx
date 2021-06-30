package me.leon.ext

import me.leon.view.EncodeType

val typeMap = mapOf(
    "base64" to EncodeType.Base64,
    "base64 safe" to EncodeType.Base64Safe,
    "urlEncode" to EncodeType.UrlEncode,
    "hex" to EncodeType.Hex,
    "base32" to EncodeType.Base32,
    "base16" to EncodeType.Base16,
    "unicode" to EncodeType.Unicode,
    "binary" to EncodeType.Binary,
)

fun String.encodeType() = typeMap[this] ?: EncodeType.Base64



