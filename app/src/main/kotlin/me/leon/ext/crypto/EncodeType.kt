package me.leon.ext.crypto

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import me.leon.encode.*
import me.leon.encode.base.*
import me.leon.ext.*

enum class EncodeType(val type: String, val defaultDict: String = "") : IEncode {
    HEX("hex") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.replace("""\\x|\s|0x|\\""".toRegex(), "").hex2ByteArray()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) = bytes.toHex()
    },
    BASE64("base64", BASE64_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base64Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base64(dict)
    },
    BASE16("base16", BASE16_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base16Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base16(dict)
    },
    BASE32("base32", BASE32_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base32Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base32(dict)
    },
    BASE36("base36(radix36)", BASE36_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base36Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base36(dict)
    },
    BASE58("base58(radix58)", BASE58_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base58Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base58(dict)
    },
    BASE58_CHECK("base58Check", BASE58_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base58CheckDecode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base58Check(dict)
    },
    BASE85_IPV6("base85_IPv6", BASE85_IPV6_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base85Decode(dict.ifEmpty { BASE85_IPV6_DICT })

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base85(dict.ifEmpty { BASE85_IPV6_DICT })
    },
    BASE62("base62", BASE62_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base62Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base62(dict)
    },
    BASE69("base69", BASE69_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base69Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base69(dict)
    },
    BASE91("base91", BASE91_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base91Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base91(dict)
    },
    BASE92("base92", BASE92_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base92Decode(dict, charset)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base92Encode(dict, charset)
    },
    BASE64_URL("base64Url", BASE64_URL_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base64UrlDecode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base64Url(dict)
    },
    BASE85("base85(ASCII85)", BASE85_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base85Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base85(dict)
    },
    Z85("Z85(ZeroMQ)", Z85_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base85Decode(dict.ifEmpty { Z85_DICT })

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base85(dict.ifEmpty { Z85_DICT })
    },
    BASE100("base100", "") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base100Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base100()
    },
    BASE65536("base65536", "") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base65536Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base65536()
    },
    RADIX64("radix64") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.radix64Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.radix64()
    },
    RADIX8("radix8") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.radix8Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) = bytes.radix8()
    },
    RADIX9("radix9") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.radix9Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) = bytes.radix9()
    },
    RADIX_N("radixN") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.radixNDecode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.radixNEncode(dict)
    },
    RADIX10("radix10") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.radix10Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.radix10()
    },
    RADIX32("radix32") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.radix32Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.radix32()
    },
    URL_ENCODE("urlencode") {
        override fun decode(encoded: String, dict: String, charset: String) =
            (URLDecoder.decode(encoded, charset.ifEmpty { "UTF-8" }).orEmpty()).toByteArray(
                Charset.forName(charset)
            )

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            URLEncoder.encode(bytes.toString(Charset.forName(charset)), charset.ifEmpty { "UTF-8" })
                ?.replace("+", "%20")
                .orEmpty()
    },
    UNICODE("unicode") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.unicodeMix2String().toByteArray(Charset.forName(charset))

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(Charset.forName(charset)).toUnicodeString()
    },
    DECIMAL("decimal(ASCII)") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.decimalDecode(charset)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.decimal(charset)
    },
    OCTAL("octal") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.octalDecode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.octal(charset)
    },
    BINARY("binary") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.binary2ByteArray()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toBinaryString()
    },
    BASE45("base45", BASE45_DICT) {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base45Decode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base45(dict)
    },
    JS_HEX_ENCODE("jsHex(shell code)") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.jsHexDecodeString().toByteArray(Charset.forName(charset))

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(Charset.forName(charset)).toJsHexEncodeString()
    },
    JS_OCTAL_ENCODE("jsOctal") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.jsOctalDecodeString().toByteArray(Charset.forName(charset))

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(Charset.forName(charset)).toJsOctalEncodeString()
    },
    ESCAPE("escape") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.unescape(charset)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.escape(charset)
    },
    ESCAPE_ALL("escapeAll") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.unescape(charset)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.escapeAll(charset)
    },
    UUENCODE("uuEncode") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.uuDecode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.uuEncode(dict)
    },
    XXENCODE("xxEncode") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.xxDecode(dict)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.xxEncode(dict)
    },
    QUOTE_PRINTABLE("quotedPrintable") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.quotePrintableDecode(charset)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.quotePrintable(charset)
    },
    PUNY_CODE("punyCode") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.punyCodeDecode(charset)

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.punyCode(charset)
    },
    HTML_ENTITY("htmlEntity") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.htmlEntity2String().toByteArray(Charset.forName(charset))

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(Charset.forName(charset)).toHtmlEntity(isAll = false)
    },
    HTML_ENTITY_ALL("htmlEntityAll") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.htmlEntity2String().toByteArray(Charset.forName(charset))

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(Charset.forName(charset)).toHtmlEntity(isAll = true)
    },
    HEX_REVERSE("hexReverse") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.replace("""\\x|\s|0x|\\""".toRegex(), "").hexReverse2ByteArray()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toHexReverse()
    },
    UTF7("utf7") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.uft7Decode().toByteArray()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(charset.toCharset()).utf7()
    },
    UTF7_ALL("utf7 all") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.uft7Decode().toByteArray()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(charset.toCharset()).utf7(true)
    },
    UTF7_EXT("utf7(imap)") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.uft7ExtDecode().toByteArray()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.toString(charset.toCharset()).utf7Ext()
    },
    MIX_HEX_OCT_BIN("HexOctBin") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.mixDecode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.mixEncode()
    },
    ECOJI("ecoji") {
        override fun decode(encoded: String, dict: String, charset: String) = encoded.ecojiDecode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) = bytes.ecoji()
    },
    BASE2048("base2048") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base2048Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base2048()
    },
    BASE32768("basa32768") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.base32768Decode()

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.base32768()
    },
    DECIMAL_RADIX_N("radix") {
        override fun decode(encoded: String, dict: String, charset: String) =
            encoded.radixNDecode2Decimal(dict.map { it.toString() })

        override fun encode2String(bytes: ByteArray, dict: String, charset: String) =
            bytes.decodeToString().toBigInteger().radixNEncode(dict)
    },
}
