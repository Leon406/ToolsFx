package me.leon.controller

import java.nio.charset.Charset
import me.leon.ext.*
import me.leon.ext.crypto.EncodeType
import tornadofx.*

class EncodeController : Controller() {

    fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8",
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine)
            raw.lineAction2String {
                encode2String(raw.toByteArray(Charset.forName(charset)), type, dic, charset)
            }
        else encode2String(raw.toByteArray(Charset.forName(charset)), type, dic, charset)

    fun encode2String(
        raw: ByteArray,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): String =
        catch({ "编码错误: $it" }) {
            println("encode2String $type $dic $charset")
            if (raw.isEmpty()) "" else type.encode2String(raw, dic, charset)
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8",
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine)
            encoded.lineAction2String {
                decode(it, type, dic, charset).toString(Charset.forName(charset))
            }
        else decode(encoded, type, dic, charset).toString(Charset.forName(charset))

    fun decode(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): ByteArray =
        catch({ "解码错误: ${it.lineSplit().first()}".toByteArray() }) {
            println("decode $type $dic $charset $encoded")
            if (encoded.isEmpty()) byteArrayOf() else type.decode(encoded, dic, charset)
        }
}
