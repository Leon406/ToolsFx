package me.leon.controller

import java.io.File
import java.nio.charset.Charset
import me.leon.DEBUG
import me.leon.UTF8
import me.leon.ext.catch
import me.leon.ext.crypto.EncodeType
import me.leon.ext.lineAction2String
import me.leon.ext.toFile
import tornadofx.Controller

class EncodeController : Controller() {

    fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = UTF8,
        singleLine: Boolean = false,
        isFile: Boolean = false,
    ) =
        if (isFile) {
            val file = raw.toFile()
            with(encode2String(file.readBytes(), type, dic, charset)) {
                if (length > 1024 * 1024) {
                    val out = File(file.parentFile, file.name + ".enc")
                    out.writer().use { output -> output.write(this) }
                    "Output is larger than 1M, saved to ${out.absolutePath}"
                } else {
                    this
                }
            }
        } else {
            encode2String(raw, type, dic, charset, singleLine)
        }

    private fun encode2String(
        raw: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = UTF8,
        singleLine: Boolean = false
    ): String =
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
        charset: String = UTF8
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

    private fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = UTF8,
        singleLine: Boolean = false
    ): String =
        if (singleLine) {
            encoded.lineAction2String {
                decode(it, type, dic, charset).toString(Charset.forName(charset))
            }
        } else {
            decode(encoded, type, dic, charset).toString(Charset.forName(charset))
        }

    fun decode2String(
        encoded: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = UTF8,
        singleLine: Boolean = false,
        isFile: Boolean = false
    ): String =
        if (isFile && encoded.length < 1024) {
            val file = encoded.toFile()
            val name = file.name.replace(".enc", "")
            val out =
                File(
                    file.parentFile,
                    name.takeIf { file.extension.isNotEmpty() && file.name != name } ?: "$name.dec"
                )
            out.outputStream().use {
                it.write(decode(encoded.toFile().readText(), type, dic, charset))
            }
            "Decode output saved to ${out.absolutePath}"
        } else {
            decode2String(encoded, type, dic, charset, singleLine)
        }

    fun decode(
        encoded: String,
        type: EncodeType = EncodeType.BASE64,
        dic: String = "",
        charset: String = UTF8
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
