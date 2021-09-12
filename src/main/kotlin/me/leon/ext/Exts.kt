package me.leon.ext

import java.nio.charset.Charset

inline fun <reified T> Any?.safeAs(): T? = this as? T

inline fun <reified T> Any?.cast() = this as T

fun ByteArray.charsetChange(from: String, to: String) =
    String(this, Charset.forName(from)).toByteArray(Charset.forName(to))
