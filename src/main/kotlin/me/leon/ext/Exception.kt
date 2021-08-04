package me.leon.ext

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stacktrace() =
    StringWriter().apply { this@stacktrace.printStackTrace(PrintWriter(this)) }.toString()
