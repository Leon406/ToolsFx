package me.leon.encode.base

import me.leon.toBigInteger

fun String.radix10(): String = toByteArray().radix10()

fun ByteArray.radix10(): String = toBigInteger().toString(10)

fun String.radix10Decode(): ByteArray = toBigInteger(10).toByteArray()

fun String.radix10Decode2String() = radix10Decode().decodeToString()
