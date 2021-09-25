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

inline fun <T> List<T>.sliceList(split: List<Int>): MutableList<List<T>> {
    val ranges =
        split.foldIndexed(mutableListOf<IntRange>()) { index, acc, i ->
            acc.apply {
                add(
                    split.take(index).sum() until
                        (split.take(index + 1).sum().takeIf { it <= this@sliceList.size }
                            ?: this@sliceList.size)
                )
            }
        }
    return split.indices.fold(mutableListOf()) { acc, i ->
        acc.apply { acc.add(this@sliceList.slice(ranges[i])) }
    }
}

inline fun String.sliceList(split: List<Int>, separator: String = " ") =
    toList().sliceList(split).joinToString(separator) { it.joinToString("") }
