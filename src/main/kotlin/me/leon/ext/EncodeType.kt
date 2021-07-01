package me.leon.ext

enum class EncodeType(type: String) {
    Base64("base64"),
    Base64Safe("base64 safe"),
    Unicode("unicode"),
    Hex("hex"),
    Binary("binary"),
    UrlEncode("urlencode"),
    Base32("base32"),
    Base16("base16"),
}