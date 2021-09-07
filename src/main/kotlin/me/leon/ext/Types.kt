package me.leon.ext

val encodeTypeMap = EncodeType.values().associateBy { it.type }

fun String.encodeType() = encodeTypeMap[this] ?: EncodeType.Base64
