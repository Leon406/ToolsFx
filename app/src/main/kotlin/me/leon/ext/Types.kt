package me.leon.ext

import me.leon.compress.Compression

val encodeTypeMap = EncodeType.values().associateBy { it.type }

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.Base64

val classicalTypeMap = ClassicalCryptoType.values().associateBy { it.type }

fun String.classicalType() = classicalTypeMap[this] ?: ClassicalCryptoType.CAESAR

val compressTypeMap = Compression.values().associateBy { it.alg }

fun String.compressType() = compressTypeMap[this] ?: Compression.GZIP
