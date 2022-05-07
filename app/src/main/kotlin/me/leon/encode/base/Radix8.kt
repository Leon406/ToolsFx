package me.leon.encode.base

import me.leon.toBigInteger

fun ByteArray.radix8(): String = toBigInteger().toString(8)

fun String.radix8Decode(): ByteArray = toBigInteger(8).toByteArray()
