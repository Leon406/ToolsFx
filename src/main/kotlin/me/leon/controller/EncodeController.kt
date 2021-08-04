package me.leon.controller

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import me.leon.base.base16
import me.leon.base.base16Decode
import me.leon.base.base32
import me.leon.base.base32Decode
import me.leon.ext.*
import tornadofx.*

class EncodeController : Controller() {
    fun encode(raw: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (raw.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 -> Base64.getEncoder().encodeToString(raw.toByteArray())
                    EncodeType.Base64Safe ->
                        Base64.getUrlEncoder().encodeToString(raw.toByteArray())
                    EncodeType.Hex -> raw.toByteArray(Charsets.UTF_8).toHex()
                    EncodeType.UrlEncode -> URLEncoder.encode(raw).replace("+", "%20")
                    EncodeType.Base32 -> raw.base32()
                    EncodeType.Base16 -> raw.base16()
                    EncodeType.Unicode -> raw.toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                }
        } catch (e: Exception) {
            "编码错误: ${e.stacktrace()}"
        }

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (encoded.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 ->
                        Base64.getDecoder().decode(encoded).toString(Charset.defaultCharset())
                    EncodeType.Base64Safe ->
                        Base64.getUrlDecoder().decode(encoded).toString(Charset.defaultCharset())
                    EncodeType.Hex -> encoded.hex2Ascii()
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded)
                    EncodeType.Base32 -> encoded.base32Decode()
                    EncodeType.Base16 -> encoded.base16Decode()
                    EncodeType.Unicode -> encoded.unicode2String()
                    EncodeType.Binary -> encoded.binary2Ascii()
                }
        } catch (e: Exception) {
            "解码错误: ${e.stacktrace()}"
        }
}
