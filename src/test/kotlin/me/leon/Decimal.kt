package me.leon

fun String.decimal() = toByteArray().decimal()

fun String.decimal2() = toByteArray().decimal2()

fun ByteArray.decimal() = joinToString(" ") { (it.toInt()).toString() }

fun ByteArray.decimal2() = String(this).toCharArray().joinToString(" ") { (it.code).toString() }

fun String.decimalDecode() = split(" +".toRegex()).map { it.toByte() }.toByteArray()

fun String.decimalDecode2String() = String(decimalDecode())

fun String.decimalDecode2() = decimalDecode2String2().toByteArray()

fun String.decimalDecode2String2() = split(" +".toRegex()).map { Char(it.toInt()) }.toString()
