package me.leon.encode

fun String.octal() = toByteArray().octal()

fun ByteArray.octal() = String(this).toCharArray().joinToString(" ") { (it.code).toString(8) }

fun String.octalDecode() = octalDecode2String().toByteArray()

fun String.octalDecode2String() = split(" +".toRegex()).map { Char(it.toInt(8)) }.joinToString("")
