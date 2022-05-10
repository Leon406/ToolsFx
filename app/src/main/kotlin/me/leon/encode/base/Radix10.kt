package me.leon.encode.base

private const val RADIX = 10
private const val RADIX10_DICT = "0123456789"

fun ByteArray.radix10(): String = radixNEncode(RADIX, RADIX10_DICT)

fun String.radix10Decode(): ByteArray = radixNDecode(RADIX, RADIX10_DICT)
