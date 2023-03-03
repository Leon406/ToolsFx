package me.leon

const val UTF8 = "UTF-8"
val CHARSETS = listOf("UTF-8", "ISO8859-1", "GBK", "GB2312", "GB18030")

const val P1 = "p1"
const val P2 = "p2"
const val C1 = "c1"
const val C2 = "c2"
const val DEBUG = true
val REG_NON_PRINTABLE = "[\u0000-\u0009\u000B\u000C\u000E-\u001F]|解码错误:|�".toRegex()
val REG_NUMBER = "\\d+".toRegex()
val REG_CRACK_HEADER = "\\d+ [\\w)]+(-->\\d+ )?".toRegex()

val build: String
    get() = "2023/03/03"

val appVersion: String
    get() = "1.15.4.beta"
