package me.leon.encode

import java.nio.charset.Charset

fun String.decimal(charset: String = "UTF-8") =
    toByteArray(Charset.forName(charset)).decimal(charset)

fun ByteArray.decimal(charset: String = "UTF-8") =
    toString(Charset.forName(charset)).asIterable().joinToString(" ") { (it.code).toString() }

fun String.decimalDecode(charset: String = "UTF-8") =
    decimalDecode2String().toByteArray(Charset.forName(charset))

fun String.decimalDecode2String() =
    split("\\D+".toRegex()).filterNot { it.isEmpty() }.map { Char(it.toInt()) }.joinToString("")
