package me.leon.encode.base

import java.nio.charset.Charset

const val RADIX64_DICT = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

fun String.radix64(charset: Charset = Charsets.UTF_8) =
    toByteArray(charset).base64(RADIX64_DICT, false)

fun ByteArray.radix64() = base64(RADIX64_DICT, false)

fun String.radix64Decode() = base64Decode(RADIX64_DICT)
