package me.leon.encode.base

import me.leon.toBigInteger

fun String.radix32(): String = toByteArray().radix32()

fun ByteArray.radix32(): String = toBigInteger().toString(32)

fun String.radix32Decode(): ByteArray = toBigInteger(32).toByteArray()

fun String.radix32Decode2String() = radix32Decode().decodeToString()
