package me.leon.ext

import me.leon.encode.base.*

enum class EncodeType(val type: String, val dic: String = "") {
    Base64("base64", BASE64_DICT),
    UrlEncode("urlencode"),
    Unicode("unicode"),
    Hex("hex"),
    Decimal("decimal"),
    Octal("octal"),
    Binary("binary"),
    Base64Safe("urlBase64", BASE64_URL_DICT),
    Base16("base16", BASE16_DICT),
    Base32("base32", BASE32_DICT),
    Base36("base36", BASE36_DICT),
    Base58("base58", BASE58_DICT),
    Base58Check("base58Check", BASE58_DICT),
    Base62("base62", BASE62_DICT),
    Base85("base85", BASE85_DICT),
    Base91("base91", BASE91_DICT),
    Base92("base92", BASE92_DICT),
    Escape("escape");

    fun decode(){
        //if needed
    }
}
