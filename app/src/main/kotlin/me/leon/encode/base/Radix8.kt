package me.leon.encode.base

private const val RADIX = 8
private const val RADIX8_DICT = "0123456789"

fun ByteArray.radix8(): String = radixNEncode(RADIX, RADIX8_DICT)

fun String.radix8Decode(): ByteArray = radixNDecode(RADIX, RADIX8_DICT)
