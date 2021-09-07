package me.leon.encode

fun String.decimal() = toByteArray().decimal()

fun ByteArray.decimal() = String(this).toCharArray().joinToString(" ") { (it.code).toString() }

fun String.decimalDecode() = decimalDecode2String().toByteArray()

fun String.decimalDecode2String() = split(" +".toRegex()).map { Char(it.toInt()) }.joinToString("")
