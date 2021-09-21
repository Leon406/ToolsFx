package me.leon.controller

import java.nio.charset.Charset
import me.leon.ext.*
import tornadofx.*

class EncodeController : Controller() {

    fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8",
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine) raw.lineAction2String { encode2String(it, type, dic, charset) }
        else encode2String(raw, type, dic, charset)

    private fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): String = encode2String(raw.toByteArray(Charset.forName(charset)), type, dic, charset)

    fun encode2String(
        raw: ByteArray,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): String =
        catch({ "编码错误: $it" }) {
            println("encode2String $type $dic $charset ${String(raw, Charset.forName(charset))}")
            if (raw.isEmpty()) "" else type.encode2String(raw, dic, charset)
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8",
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine) encoded.lineAction2String { decode2String(it, type, dic, charset) }
        else decode2String(encoded, type, dic, charset)

    private fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ) = String(decode(encoded, type, dic, charset), Charset.forName(charset))

    fun decode(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): ByteArray =
        catch({ "解码错误: $it".toByteArray() }) {
            println("decode $type $dic $charset $encoded")
            if (encoded.isEmpty()) byteArrayOf() else type.decode(encoded, dic, charset)
        }
}
