package me.leon.encode

import java.nio.charset.Charset

fun String.octal(charset: String = "UTF-8") = toByteArray().octal(charset)

fun ByteArray.octal(charset: String = "UTF-8") =
    toString(Charset.forName(charset)).toCharArray().joinToString(" ") { (it.code).toString(8) }

fun String.octalDecode() = octalDecode2String().toByteArray()

fun String.octalDecode2String() =
    split("[^\\d+]".toRegex()).filter { it.isNotEmpty() }.map { Char(it.toInt(8)) }.joinToString("")
