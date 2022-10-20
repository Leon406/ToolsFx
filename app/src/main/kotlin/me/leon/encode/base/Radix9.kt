package me.leon.encode.base

private const val RADIX10_DICT = "012345678"

fun ByteArray.radix9(): String = radixNEncode(RADIX10_DICT)

fun String.radix9Decode(): ByteArray = radixNDecode(RADIX10_DICT)
