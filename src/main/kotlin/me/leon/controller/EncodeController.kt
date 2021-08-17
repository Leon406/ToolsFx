package me.leon.controller

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import me.leon.base.*
import me.leon.ext.*
import tornadofx.*

class EncodeController : Controller() {
    fun encode2String(raw: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (raw.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 -> Base64.getEncoder().encodeToString(raw.toByteArray())
                    EncodeType.Base64Safe ->
                        Base64.getUrlEncoder().encodeToString(raw.toByteArray())
                    EncodeType.Hex -> raw.toByteArray().toHex()
                    EncodeType.UrlEncode -> URLEncoder.encode(raw)?.replace("+", "%20")?:""
                    EncodeType.Base32 -> raw.base32()
                    EncodeType.Base16 -> raw.base16()
                    EncodeType.Unicode -> raw.toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                }
        } catch (e: Exception) {
            "编码错误: ${e.stacktrace()}"
        }

    fun encode2String(raw: ByteArray, type: EncodeType = EncodeType.Base64): String =
        try {
            if (raw.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 -> Base64.getEncoder().encodeToString(raw)
                    EncodeType.Base64Safe -> Base64.getUrlEncoder().encodeToString(raw)
                    EncodeType.Hex -> raw.toHex()
                    EncodeType.UrlEncode -> URLEncoder.encode(raw.toString())
                        ?.replace("+", "%20")?:""
                    EncodeType.Base32 -> raw.base32()
                    EncodeType.Base16 -> raw.base16()
                    EncodeType.Unicode -> String(raw, Charsets.UTF_8).toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                }
        } catch (e: Exception) {
            "编码错误: ${e.stacktrace()}"
        }

    fun decode2String(encoded: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (encoded.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 ->
                        Base64.getDecoder().decode(encoded).toString(Charsets.UTF_8)
                    EncodeType.Base64Safe ->
                        Base64.getUrlDecoder().decode(encoded).toString(Charsets.UTF_8)
                    EncodeType.Hex -> encoded.hex2Ascii()
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded)?:""
                    EncodeType.Base32 -> encoded.base32Decode2String()
                    EncodeType.Base16 -> encoded.base16Decode2String()
                    EncodeType.Unicode -> encoded.unicode2String()
                    EncodeType.Binary -> encoded.binary2Ascii()
                }
        } catch (e: Exception) {
            "解码错误: ${e.stacktrace()}"
        }

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64): ByteArray =
        try {
            if (encoded.isEmpty()) byteArrayOf()
            else
                when (type) {
                    EncodeType.Base64 -> Base64.getDecoder().decode(encoded)
                    EncodeType.Base64Safe -> Base64.getUrlDecoder().decode(encoded)
                    EncodeType.Hex -> encoded.hex2ByteArray()
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded)?.toByteArray()?: byteArrayOf()
                    EncodeType.Base32 -> encoded.base32Decode()
                    EncodeType.Base16 -> encoded.base16Decode()
                    EncodeType.Unicode -> encoded.unicode2String().toByteArray()
                    EncodeType.Binary -> encoded.binary2ByteArray()
                }
        } catch (e: Exception) {
            "解码错误: ${e.stacktrace()}".toByteArray()
        }
}
