package me.leon.ext

val encodeTypeMap =
    mapOf(
        "base64" to EncodeType.Base64,
        "urlBase64" to EncodeType.Base64Safe,
        "urlEncode" to EncodeType.UrlEncode,
        "hex" to EncodeType.Hex,
        "base32" to EncodeType.Base32,
        "base16" to EncodeType.Base16,
        "unicode" to EncodeType.Unicode,
        "binary" to EncodeType.Binary,
        "base58" to EncodeType.Base58,
        "base58Check" to EncodeType.Base58Check,
    )

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.Base64
