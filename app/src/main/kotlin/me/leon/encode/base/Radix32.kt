package me.leon.encode.base

import me.leon.toBigInteger

fun ByteArray.radix32(): String = toBigInteger().toString(32)

fun String.radix32Decode(): ByteArray = toBigInteger(32).toByteArray()
