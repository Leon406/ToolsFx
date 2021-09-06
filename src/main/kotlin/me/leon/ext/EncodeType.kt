package me.leon.ext

enum class EncodeType(val type: String) {
    Base64("base64"),
    UrlEncode("urlencode"),
    Unicode("unicode"),
    Hex("hex"),
    Binary("binary"),
    Base64Safe("urlBase64"),
    Base16("base16"),
    Base32("base32"),
    Base36("base36"),
    Base58("base58"),
    Base58Check("base58Check"),
    Base62("base62"),
    Base85("base85"),
    Base91("base91"),
    Base92("base92"),
}
