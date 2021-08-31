package me.leon.controller

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Base64
import me.leon.base.base16
import me.leon.base.base16Decode
import me.leon.base.base16Decode2String
import me.leon.base.base32
import me.leon.base.base32Decode
import me.leon.base.base32Decode2String
import me.leon.base.base58
import me.leon.base.base58Check
import me.leon.base.base58CheckDecode
import me.leon.base.base58CheckDecode2String
import me.leon.base.base58Decode
import me.leon.base.base58Decode2String
import me.leon.ext.EncodeType
import me.leon.ext.binary2Ascii
import me.leon.ext.binary2ByteArray
import me.leon.ext.hex2Ascii
import me.leon.ext.hex2ByteArray
import me.leon.ext.stacktrace
import me.leon.ext.toBinaryString
import me.leon.ext.toHex
import me.leon.ext.toUnicodeString
import me.leon.ext.unicode2String
import tornadofx.Controller

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
                    EncodeType.UrlEncode -> URLEncoder.encode(raw)?.replace("+", "%20") ?: ""
                    EncodeType.Base32 -> raw.base32()
                    EncodeType.Base16 -> raw.base16()
                    EncodeType.Unicode -> raw.toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                    EncodeType.Base58 -> raw.base58()
                    EncodeType.Base58Check -> raw.base58Check()
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
                    EncodeType.UrlEncode -> URLEncoder.encode(String(raw))?.replace("+", "%20")
                            ?: ""
                    EncodeType.Base32 -> raw.base32()
                    EncodeType.Base16 -> raw.base16()
                    EncodeType.Unicode -> String(raw, Charsets.UTF_8).toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                    EncodeType.Base58 -> raw.base58()
                    EncodeType.Base58Check -> raw.base58Check()
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
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded) ?: ""
                    EncodeType.Base32 -> encoded.base32Decode2String()
                    EncodeType.Base16 -> encoded.base16Decode2String()
                    EncodeType.Unicode -> encoded.unicode2String()
                    EncodeType.Binary -> encoded.binary2Ascii()
                    EncodeType.Base58 -> encoded.base58Decode2String()
                    EncodeType.Base58Check -> encoded.base58CheckDecode2String()
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
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded)?.toByteArray()
                            ?: byteArrayOf()
                    EncodeType.Base32 -> encoded.base32Decode()
                    EncodeType.Base16 -> encoded.base16Decode()
                    EncodeType.Unicode -> encoded.unicode2String().toByteArray()
                    EncodeType.Binary -> encoded.binary2ByteArray()
                    EncodeType.Base58 -> encoded.base58Decode()
                    EncodeType.Base58Check -> encoded.base58CheckDecode()
                }
        } catch (e: Exception) {
            "解码错误: ${e.stacktrace()}".toByteArray()
        }
}