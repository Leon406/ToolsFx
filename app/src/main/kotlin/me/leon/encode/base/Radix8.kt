package me.leon.encode.base

private const val RADIX8_DICT = "01234567"

fun ByteArray.radix8(): String = radixNEncode(RADIX8_DICT)

fun String.radix8Decode(): ByteArray = radixNDecode(RADIX8_DICT)
