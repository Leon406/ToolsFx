package me.leon.encode

import java.net.IDN
import java.nio.charset.Charset

fun String.punyCode(charset: String = "UTF-8") = toByteArray().punyCode(charset)

fun ByteArray.punyCode(charset: String = "UTF-8"): String =
    IDN.toASCII(toString(Charset.forName(charset)))

fun String.punyCodeDecode(charset: String = "UTF-8") =
    punyCodeDecode2String().toByteArray(Charset.forName(charset))

fun String.punyCodeDecode2String(): String = IDN.toUnicode(this)
