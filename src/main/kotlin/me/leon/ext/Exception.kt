package me.leon.ext

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stacktrace() =
    StringWriter().apply { this@stacktrace.printStackTrace(PrintWriter(this)) }.toString()

fun <R> catch(defaultValue: (String) -> R, block: () -> R) =
    runCatching { block.invoke() }.getOrElse { defaultValue.invoke(it.stacktrace()) }
