package me.leon.ext

import java.nio.charset.Charset

inline fun <reified T> Any?.safeAs(): T? = this as? T

inline fun <reified T> Any?.cast() = this as T

fun ByteArray.charsetChange(from: String, to: String) =
    String(this, Charset.forName(from)).toByteArray(Charset.forName(to))

fun String.lineAction2String(action: (String) -> String) =
    split("\n|\r\n".toRegex()).joinToString("\n") { action.invoke(it) }

inline fun <T> String.lineAction(action: (String) -> T) =
    split("\n|\r\n".toRegex()).map { action.invoke(it) }

fun String.lineSplit() = split("\n|\r\n".toRegex())

fun String.lineCount() = split("\n|\r\n".toRegex()).size

fun String.lineActionIndex(action: (String, Int) -> String) =
    split("\n|\r\n".toRegex()).mapIndexed { index, s -> action.invoke(s, index) }.joinToString("\n")
