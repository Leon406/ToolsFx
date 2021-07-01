package me.leon.ext


val encodeTypeMap = mapOf(
    "base64" to EncodeType.Base64,
    "base64 safe" to EncodeType.Base64Safe,
    "urlEncode" to EncodeType.UrlEncode,
    "hex" to EncodeType.Hex,
    "base32" to EncodeType.Base32,
    "base16" to EncodeType.Base16,
    "unicode" to EncodeType.Unicode,
    "binary" to EncodeType.Binary,
)

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.Base64

val digestTypeMap = mapOf(
    "MD2" to HashType.MD2,
    "MD4" to HashType.MD4,
    "MD5" to HashType.MD5,
    "SHA1" to HashType.SHA1,
    "SHA2" to HashType.SHA2,
    "SHA3" to HashType.SHA3,
    "SM3" to HashType.SM3,
    "Whirlpool" to HashType.WHIRLPOOL,
    "Tiger" to HashType.TIGER,
    "RIPEMD" to HashType.RIPEMD,
)

fun String.digestType() = digestTypeMap[this] ?: HashType.MD5



