package me.leon.controller

import java.nio.charset.Charset
import me.leon.DEBUG
import me.leon.ext.catch
import me.leon.ext.crypto.EncodeType
import me.leon.ext.lineAction2String
import tornadofx.*

class EncodeController : Controller() {

    fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = "UTF-8",
        singleLine: Boolean = false
    ) =
        if (singleLine) {
            raw.lineAction2String {
                encode2String(it.toByteArray(Charset.forName(charset)), type, dic, charset)
            }
        } else {
            encode2String(raw.toByteArray(Charset.forName(charset)), type, dic, charset)
        }

    fun encode2String(
        raw: ByteArray,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = "UTF-8"
    ): String =
        catch({ "编码错误: $it" }) {
            if (DEBUG) {
                println("encode2String $type $dic $charset")
            }
            if (raw.isEmpty()) {
                ""
            } else {
                type.encode2String(raw, dic, charset)
            }
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = "UTF-8",
        singleLine: Boolean = false
    ) =
        if (singleLine) {
            encoded.lineAction2String {
                decode(it, type, dic, charset).toString(Charset.forName(charset))
            }
        } else {
            decode(encoded, type, dic, charset).toString(Charset.forName(charset))
        }

    fun decode(
        encoded: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = "UTF-8"
    ): ByteArray =
        catch({ "解码错误: ${it.lines().first()}".toByteArray() }) {
            if (DEBUG) {
                println("decode $type $dic $charset ${encoded.length}")
            }
            if (encoded.isEmpty()) {
                byteArrayOf()
            } else {
                type.decode(encoded, dic, charset)
            }
        }
}
