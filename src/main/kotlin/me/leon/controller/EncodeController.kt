package me.leon.controller

import java.net.URLDecoder
import java.net.URLEncoder
import me.leon.base.*
import me.leon.base.base92Decode
import me.leon.base.base92Encode
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
    fun encode2String(raw: String, type: EncodeType = EncodeType.Base64, dic: String = ""): String =
        encode2String(raw.toByteArray(), type, dic)

    fun encode2String(
        raw: ByteArray,
        type: EncodeType = EncodeType.Base64,
        dic: String = ""
    ): String =
        catch({ "编码错误: $it" }) {
            println("$type $dic")
            if (raw.isEmpty()) ""
            else
                when (type) {
                    EncodeType.Base64 -> raw.base64(dic)
                    EncodeType.Base64Safe -> raw.safeBase64(dic)
                    EncodeType.Hex -> raw.toHex()
                    EncodeType.UrlEncode -> URLEncoder.encode(String(raw))?.replace("+", "%20")
                            ?: ""
                    EncodeType.Unicode -> String(raw, Charsets.UTF_8).toUnicodeString()
                    EncodeType.Binary -> raw.toBinaryString()
                    EncodeType.Base16 -> raw.base16(dic)
                    EncodeType.Base32 -> raw.base32(dic)
                    EncodeType.Base36 -> raw.base36(dic)
                    EncodeType.Base58 -> raw.base58(dic)
                    EncodeType.Base58Check -> raw.base58Check(dic)
                    EncodeType.Base62 -> raw.base62(dic)
                    EncodeType.Base85 -> raw.base85(dic)
                    EncodeType.Base91 -> raw.base91(dic)
                    EncodeType.Base92 -> raw.base92Encode(dic)
                }
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = ""
    ): String = String(decode(encoded, type, dic))

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64, dic: String = ""): ByteArray =
        catch({ "解码错误: $it".toByteArray() }) {
            if (encoded.isEmpty()) byteArrayOf()
            else
                when (type) {
                    EncodeType.Base64 -> encoded.base64Decode(dic)
                    EncodeType.Base64Safe -> encoded.safeBase64Decode(dic)
                    EncodeType.Hex ->
                        encoded.replace("""\\x|\s|0x|\\""".toRegex(), "").hex2ByteArray()
                    EncodeType.UrlEncode -> URLDecoder.decode(encoded)?.toByteArray()
                            ?: byteArrayOf()
                    EncodeType.Unicode -> encoded.unicode2String().toByteArray()
                    EncodeType.Binary -> encoded.binary2ByteArray()
                    EncodeType.Base16 -> encoded.base16Decode(dic)
                    EncodeType.Base32 -> encoded.base32Decode(dic)
                    EncodeType.Base36 -> encoded.base36Decode(dic)
                    EncodeType.Base58 -> encoded.base58Decode(dic)
                    EncodeType.Base58Check -> encoded.base58CheckDecode(dic)
                    EncodeType.Base62 -> encoded.base62Decode(dic)
                    EncodeType.Base85 -> encoded.base85Decode(dic)
                    EncodeType.Base91 -> encoded.base91Decode(dic)
                    EncodeType.Base92 -> encoded.base92Decode(dic)
                }
        }
}
