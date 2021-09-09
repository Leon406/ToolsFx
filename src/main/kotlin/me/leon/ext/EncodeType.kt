package me.leon.ext

import me.leon.encode.base.BASE16_DICT
import me.leon.encode.base.BASE32_DICT
import me.leon.encode.base.BASE36_DICT
import me.leon.encode.base.BASE58_DICT
import me.leon.encode.base.BASE62_DICT
import me.leon.encode.base.BASE64_DICT
import me.leon.encode.base.BASE64_URL_DICT
import me.leon.encode.base.BASE85_DICT
import me.leon.encode.base.BASE91_DICT
import me.leon.encode.base.BASE92_DICT
import me.leon.encode.base.base16
import me.leon.encode.base.base16Decode
import me.leon.encode.base.base32
import me.leon.encode.base.base32Decode
import me.leon.encode.base.base36
import me.leon.encode.base.base36Decode
import me.leon.encode.base.base58
import me.leon.encode.base.base58Check
import me.leon.encode.base.base58CheckDecode
import me.leon.encode.base.base58Decode
import me.leon.encode.base.base62
import me.leon.encode.base.base62Decode
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.encode.base.base85
import me.leon.encode.base.base85Decode
import me.leon.encode.base.base91
import me.leon.encode.base.base91Decode
import me.leon.encode.base.base92Decode
import me.leon.encode.base.base92Encode
import me.leon.encode.base.safeBase64Decode
import me.leon.encode.decimal
import me.leon.encode.decimalDecode
import me.leon.encode.escape
import me.leon.encode.octal
import me.leon.encode.octalDecode
import me.leon.encode.unescape
import java.net.URLEncoder

enum class EncodeType(val type: String, val defaultDict: String = "") {
    Base64("base64", BASE64_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base64Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base64(dict)
    },
    UrlEncode("urlencode") {
        override fun decode(encoded: String, dict: String) = encoded.base64Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) =
            URLEncoder.encode(String(bytes))?.replace("+", "%20") ?: ""
    },
    Unicode("unicode") {
        override fun decode(encoded: String, dict: String) = encoded.unicode2String().toByteArray()
        override fun encode2String(bytes: ByteArray, dict: String) =
            String(bytes, Charsets.UTF_8).toUnicodeString()
    },
    Hex("hex") {
        override fun decode(encoded: String, dict: String) =
            encoded.replace("""\\x|\s|0x|\\""".toRegex(), "").hex2ByteArray()
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.toHex()
    },
    Decimal("decimal") {
        override fun decode(encoded: String, dict: String) = encoded.decimalDecode()
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.decimal()
    },
    Octal("octal") {
        override fun decode(encoded: String, dict: String) = encoded.octalDecode()
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.octal()
    },
    Binary("binary") {
        override fun decode(encoded: String, dict: String) = encoded.binary2ByteArray()
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.toBinaryString()
    },
    Base64Safe("urlBase64", BASE64_URL_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.safeBase64Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) =
            URLEncoder.encode(String(bytes))?.replace("+", "%20") ?: ""
    },
    Base16("base16", BASE16_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base16Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base16(dict)
    },
    Base32("base32", BASE32_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base32Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base32(dict)
    },
    Base36("base36", BASE36_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base36Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base36(dict)
    },
    Base58("base58", BASE58_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base58Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base58(dict)
    },
    Base58Check("base58Check", BASE58_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base58CheckDecode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base58Check(dict)
    },
    Base62("base62", BASE62_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base62Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base62(dict)
    },
    Base85("base85", BASE85_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base85Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base85(dict)
    },
    Base91("base91", BASE91_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base91Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base91(dict)
    },
    Base92("base92", BASE92_DICT) {
        override fun decode(encoded: String, dict: String) = encoded.base92Decode(dict)
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.base92Encode(dict)
    },
    Escape("escape") {
        override fun decode(encoded: String, dict: String) = encoded.unescape()
        override fun encode2String(bytes: ByteArray, dict: String) = bytes.escape()
    };

    abstract fun decode(encoded: String, dic: String): ByteArray
    abstract fun encode2String(bytes: ByteArray, dic: String): String
}
