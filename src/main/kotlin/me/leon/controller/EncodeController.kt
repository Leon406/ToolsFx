package me.leon.controller

import me.leon.ext.EncodeType
import me.leon.ext.catch
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
            if (raw.isEmpty()) "" else type.encode2String(raw, dic)
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.Base64,
        dic: String = ""
    ): String = String(decode(encoded, type, dic))

    fun decode(encoded: String, type: EncodeType = EncodeType.Base64, dic: String = ""): ByteArray =
        catch({ "解码错误: $it".toByteArray() }) {
            if (encoded.isEmpty()) byteArrayOf() else type.decode(encoded, dic)
        }
}
