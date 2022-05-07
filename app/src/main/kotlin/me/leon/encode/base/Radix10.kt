package me.leon.encode.base

import me.leon.toBigInteger

fun ByteArray.radix10(): String = toBigInteger().toString(10)

fun String.radix10Decode(): ByteArray = toBigInteger(10).toByteArray()
