package me.leon.ext

val encodeTypeMap = EncodeType.values().sortedBy { it.type.lowercase() }.associateBy { it.type }

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.Base64

val classicalTypeMap =
    ClassicalCryptoType.values().sortedBy { it.type.lowercase() }.associateBy { it.type }

fun String.classicalType() = classicalTypeMap[this] ?: ClassicalCryptoType.CAESAR
