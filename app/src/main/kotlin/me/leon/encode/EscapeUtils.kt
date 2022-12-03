package me.leon.encode

import java.nio.charset.Charset

object EscapeUtils {

    fun escape(src: String) =
        src.asIterable()
            .map {
                when {
                    it.isDigit() || it.isLowerCase() || it.isUpperCase() -> it
                    it.code < 16 -> "%0${it.code.toString(16)}"
                    it.code < 256 -> "%${it.code.toString(16)}"
                    else -> "%u${it.code.toString(16)}"
                }
            }
            .joinToString("")

    fun escapeAll(src: String) =
        src.asIterable().joinToString("") {
            when {
                it.code < 16 -> "%0${it.code.toString(16)}"
                it.code < 256 -> "%${it.code.toString(16)}"
                else -> "%u${it.code.toString(16)}"
            }
        }

    fun unescape(src: String): String {

        val tmp = StringBuilder(src.length)
        var lastPos = 0
        var pos: Int
        var ch: Char

        while (lastPos < src.length) {
            pos = src.indexOf("%", lastPos)
            if (pos == lastPos) {
                if (src[pos + 1] == 'u') {
                    ch = src.substring(pos + 2, pos + 6).toInt(16).toChar()
                    tmp.append(ch)
                    lastPos = pos + 6
                } else {
                    ch = src.substring(pos + 1, pos + 3).toInt(16).toChar()
                    tmp.append(ch)
                    lastPos = pos + 3
                }
            } else {
                lastPos =
                    if (pos == -1) {
                        tmp.append(src.substring(lastPos))
                        src.length
                    } else {
                        tmp.append(src.substring(lastPos, pos))
                        pos
                    }
            }
        }
        return tmp.toString()
    }
}

fun String.escape() = EscapeUtils.escape(this)

fun String.escapeAll() = EscapeUtils.escapeAll(this)

fun ByteArray.escape(charset: String = "UTF-8") = this.toString(Charset.forName(charset)).escape()

fun ByteArray.escapeAll(charset: String = "UTF-8") = toString(Charset.forName(charset)).escapeAll()

fun String.unescape(charset: String = "UTF-8") =
    unescape2String().toByteArray(Charset.forName(charset))

fun String.unescape2String() = EscapeUtils.unescape(this)
