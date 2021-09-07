package me.leon

fun String.octal() = toByteArray().octal()

fun String.octal2() = toByteArray().octal2()

fun ByteArray.octal() = joinToString(" ") { (it.toInt()).toString(8) }

fun ByteArray.octal2() = String(this).toCharArray().joinToString(" ") { (it.code).toString(8) }

fun String.octalDecode() = split(" +".toRegex()).map { it.toByte(8) }.toByteArray()

fun String.octalDecode2String() = String(octalDecode())

fun String.octalDecode2() = decimalDecode2String2().toByteArray()

fun String.octalDecode2String2() = split(" +".toRegex()).map { Char(it.toInt(8)) }.toString()
