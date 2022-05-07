package me.leon.encode.base

import me.leon.toBigInteger

fun String.radix8(): String = toByteArray().radix8()

fun ByteArray.radix8(): String = toBigInteger().toString(8)

fun String.radix8Decode(): ByteArray = toBigInteger(8).toByteArray()

fun String.radix8Decode2String() = radix8Decode().decodeToString()
