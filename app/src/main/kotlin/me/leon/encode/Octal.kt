package me.leon.encode

import java.nio.charset.Charset
import me.leon.encode.base.BYTE_MASK

fun String.octal(charset: String = "UTF-8") = toByteArray().octal(charset)

fun ByteArray.octal(charset: String = "UTF-8") =
    toString(Charset.forName(charset)).asIterable().joinToString(" ") { (it.code).toString(8) }

fun Byte.octal() = (this.toInt() and BYTE_MASK).toString(8)

fun String.octalDecode() = octalDecode2String().toByteArray()

fun String.octalByteDecode() = toInt(8).toByte()

fun String.octalDecode2String() =
    split("[^\\d+]".toRegex()).filter { it.isNotEmpty() }.map { Char(it.toInt(8)) }.joinToString("")
