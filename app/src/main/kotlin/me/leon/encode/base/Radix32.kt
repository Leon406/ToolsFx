package me.leon.encode.base

private const val RADIX32_DICT = "0123456789abcdefghijklmnopqrstuv"

fun ByteArray.radix32(): String = radixNEncode(RADIX32_DICT)

fun String.radix32Decode(): ByteArray = radixNDecode(RADIX32_DICT)
