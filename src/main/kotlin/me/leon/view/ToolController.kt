package me.leon.view

import me.leon.Digests
import me.leon.base.base16
import me.leon.base.base16Decode
import me.leon.base.base32
import me.leon.base.base32Decode
import me.leon.ext.*
import tornadofx.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*

class ToolController : Controller() {

    fun encode(raw: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (raw.isEmpty()) ""
            else when (type) {
                EncodeType.Base64 -> Base64.getEncoder().encodeToString(raw.toByteArray())
                EncodeType.Base64Safe -> Base64.getUrlEncoder().encodeToString(raw.toByteArray())
                EncodeType.Hex -> raw.toByteArray(Charsets.UTF_8).toHex()
                EncodeType.UrlEncode -> URLEncoder.encode(raw).replace("+", "%20")
                EncodeType.Base32 -> raw.base32()
                EncodeType.Base16 -> raw.base16()
                EncodeType.Unicode -> raw.toUnicodeString()
                EncodeType.Binary -> raw.toBinaryString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "编码错误: ${e.message}"
        }

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64): String =
        try {
            if (encoded.isEmpty()) ""
            else when (type) {
                EncodeType.Base64 -> Base64.getDecoder().decode(encoded).toString(Charset.defaultCharset())
                EncodeType.Base64Safe -> Base64.getUrlDecoder().decode(encoded).toString(Charset.defaultCharset())
                EncodeType.Hex -> encoded.hex2Ascii()
                EncodeType.UrlEncode -> URLDecoder.decode(encoded)
                EncodeType.Base32 -> encoded.base32Decode()
                EncodeType.Base16 -> encoded.base16Decode()
                EncodeType.Unicode -> encoded.unicode2String()
                EncodeType.Binary -> encoded.binary2Ascii()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "解码错误: ${e.message}"
        }

    fun digest(method: String, data: String) = if (data.isEmpty()) "" else Digests.hash(method, data)
    fun digestFile(method: String, path: String) = if (path.isEmpty()) "" else Digests.hashByFile(method, path).also { println(
        Digests.hashFile2(method, path)
    ) }
}
