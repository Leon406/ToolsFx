package me.leon.encode

import java.nio.charset.Charset
import me.leon.ext.*

object QuotePrintable {

    fun encode(src: String, charset: String = "UTF-8") =
        src.toCharArray()
            .map {
                when (it.code) {
                    in 33..60 -> it
                    in 62..127 -> it
                    in 0..15 -> "=0${it.code.toString(16)}"
                    in 128..255 -> "=${it.code.toString(16)}"
                    else ->
                        it
                            .toString()
                            .toByteArray(Charset.forName(charset))
                            .toHex()
                            .chunked(2)
                            .joinToString("") { "=$it" }
                }
            }
            .joinToString("")
            .chunked(75)
            .joinToString("=\r\n")

    fun decode(src: String, charset: String = "UTF-8"): String {

        var preHandle = src.split("=(?:\r\n|\n)".toRegex()).joinToString("")
        preHandle.also { println(it) }
        val sb = StringBuilder()
        val hex = StringBuilder()
        var curPos = 0
        var lastPos = 0
        println(preHandle.length)
        while (lastPos < preHandle.length) {
            curPos = preHandle.indexOf("=", lastPos)
            //            println("curPos $curPos $lastPos")
            if (curPos == -1) {
                sb.append(preHandle.subSequence(lastPos, preHandle.length))
                break
            } else if (curPos == lastPos) {
                hex.append(preHandle.subSequence(lastPos + 1, lastPos + 3))
                lastPos += 3
                while (preHandle[lastPos] == '=') {
                    hex.append(preHandle.subSequence(lastPos + 1, lastPos + 3).also { println(it) })
                    lastPos += 3
                }
                println("split $lastPos:$hex ")
                sb.append(
                    hex.toString().hex2String(charset) // .also { println("**$it**") }
                )
                hex.clear()
            } else {
                sb.append(preHandle.subSequence(lastPos, curPos))
                lastPos = curPos
            }
        }

        return sb.toString()
    }
}

fun String.quotePrintable() = QuotePrintable.encode(this)

fun ByteArray.quotePrintable() = String(this).quotePrintable()

fun String.quotePrintableDecode() = quotePrintableDecode2String().toByteArray()

fun String.quotePrintableDecode2String() = QuotePrintable.decode(this)

fun main() {

    "bfaab7a2b9a4bedfbcafbacf20".hex2String("gbk").also { println(it) }

    val raw = "开发工具集合 by leon406@52pojie.cn"
    val message = raw.repeat(2).quotePrintable()
    val gbkMsg = QuotePrintable.encode(raw, "gbk")
    println(gbkMsg)
    println(QuotePrintable.decode(gbkMsg, "gbk"))
    println(message.quotePrintableDecode2String())
}
