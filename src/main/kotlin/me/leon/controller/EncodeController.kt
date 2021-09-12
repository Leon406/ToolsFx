package me.leon.controller

import java.nio.charset.Charset
import me.leon.ext.EncodeType
import me.leon.ext.catch
import tornadofx.*

class EncodeController : Controller() {
    fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): String = encode2String(raw.toByteArray(Charset.forName(charset)), type, dic)

    fun encode2String(
        raw: ByteArray,
        type: EncodeType = EncodeType.Base64,
        dic: String = ""
    ): String =
        catch({ "编码错误: $it" }) {
            println("$type $dic")
            if (raw.isEmpty()) "" else type.encode2String(raw, dic)
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = "",
        charset: String = "UTF-8"
    ): String = String(decode(encoded, type, dic), Charset.forName(charset))

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64, dic: String = ""): ByteArray =
        catch({ "解码错误: $it".toByteArray() }) {
            if (encoded.isEmpty()) byteArrayOf() else type.decode(encoded, dic)
        }
}
