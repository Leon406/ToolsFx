package me.leon

const val UTF8 = "UTF-8"
val CHARSETS = listOf("UTF-8", "ISO8859-1", "GBK", "GB2312", "GB18030")

const val P1 = "p1"
const val P2 = "p2"
const val DEBUG = false
val REG_NON_PRINTABLE = "[\u0000-\u001F]|解码错误:|�".toRegex()
const val EXCEPTION_NON_PRINTABLE = "解码错误: contains non printable char!maybe wrong encoding!"
