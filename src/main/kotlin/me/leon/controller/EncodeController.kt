package me.leon.controller

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Base64
import me.leon.base.*
import me.leon.ext.EncodeType
import me.leon.ext.binary2ByteArray
import me.leon.ext.catch
import me.leon.ext.hex2ByteArray
import me.leon.ext.toBinaryString
import me.leon.ext.toHex
import me.leon.ext.toUnicodeString
import me.leon.ext.unicode2String
import tornadofx.Controller

class EncodeController : Controller() {
    fun encode2String(raw: String, type: EncodeType = EncodeType.Base64): String =
        encode2String(raw.toByteArray(), type)

    fun encode2String(raw: ByteArray, type: EncodeType = EncodeType.Base64): String =
        catch({ "编码错误: $it" }) {
            if (raw.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 -> Base64.getEncoder().encodeToString(raw)
                    EncodeType.Base64Safe -> Base64.getUrlEncoder().encodeToString(raw)
                    EncodeType.Hex -> raw.toHex()
                    EncodeType.UrlEncode -> URLEncoder.encode(String(raw))?.replace("+", "%20")
                            ?: ""
                    EncodeType.Unicode -> String(raw, Charsets.UTF_8).toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                    EncodeType.Base16 -> raw.base16()
                    EncodeType.Base32 -> raw.base32()
                    EncodeType.Base36 -> raw.base36()
                    EncodeType.Base58 -> raw.base58()
                    EncodeType.Base58Check -> raw.base58Check()
                    EncodeType.Base62 -> raw.base62()
                    EncodeType.Base85 -> raw.base85()
                    EncodeType.Base91 -> raw.base91()
                }
        }

    fun decode2String(encoded: String, type: EncodeType = EncodeType.Base64): String =
        String(decode(encoded, type))

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64): ByteArray =
        catch({ "解码错误: $it".toByteArray() }) {
            if (encoded.isEmpty()) byteArrayOf()
            else
                when (type) {
                    EncodeType.Base64 -> Base64.getDecoder().decode(encoded)
                    EncodeType.Base64Safe -> Base64.getUrlDecoder().decode(encoded)
                    EncodeType.Hex -> encoded.hex2ByteArray()
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded)?.toByteArray()
                            ?: byteArrayOf()
                    EncodeType.Unicode -> encoded.unicode2String().toByteArray()
                    EncodeType.Binary -> encoded.binary2ByteArray()
                    EncodeType.Base16 -> encoded.base16Decode()
                    EncodeType.Base32 -> encoded.base32Decode()
                    EncodeType.Base36 -> encoded.base36Decode()
                    EncodeType.Base58 -> encoded.base58Decode()
                    EncodeType.Base58Check -> encoded.base58CheckDecode()
                    EncodeType.Base62 -> encoded.base62Decode()
                    EncodeType.Base85 -> encoded.base85Decode()
                    EncodeType.Base91 -> encoded.base91Decode()
                }
        }
}
