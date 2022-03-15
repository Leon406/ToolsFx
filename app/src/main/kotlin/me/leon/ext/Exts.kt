package me.leon.ext

import java.io.File
import java.nio.charset.Charset
import me.leon.encode.base.base64

inline fun <reified T> Any?.safeAs(): T? = this as? T

inline fun <reified T> Any?.cast() = this as T

/** note: 不兼容编码转换会导致数据丢失,需要两个编码都能表示才能正常工作 */
fun ByteArray.charsetChange(from: String, to: String) =
    toString(Charset.forName(from)).toByteArray(Charset.forName(to))

fun ByteArray.padStart(length: Int, byte: Byte): ByteArray {
    if (size % length == 0) return this
    val padCount = length - size % length
    val bytes = ByteArray(size + padCount)
    repeat(padCount) { bytes[it] = byte }
    copyInto(bytes, padCount)
    return bytes
}

fun Int.toUnicodeChar(): String =
    takeIf { it < 65536 }?.toChar()?.toString()
        ?: toBigInteger().toByteArray().padStart(4, 0x00).toString(Charsets.UTF_32BE)

// Int 4个字节
fun String.unicodeCharToInt() =
    toByteArray(Charsets.UTF_32BE).fold(0) { acc, b -> acc * 256 + b.toInt().and(0xFF) }

fun String.lineAction2String(action: (String) -> String) =
    split("\n|\r\n".toRegex()).joinToString("\n") { action.invoke(it) }

inline fun <T> String.lineAction(action: (String) -> T) =
    split("\n|\r\n".toRegex()).map { action.invoke(it) }

fun String.lineSplit() = split("\n|\r\n".toRegex())

fun String.lineCount() = split("\n|\r\n".toRegex()).size

fun String.lineActionIndex(action: (String, Int) -> String) =
    split("\n|\r\n".toRegex()).mapIndexed { index, s -> action.invoke(s, index) }.joinToString("\n")

fun <T> List<T>.sliceList(split: List<Int>): MutableList<List<T>> {
    val ranges =
        split.foldIndexed(mutableListOf<IntRange>()) { index, acc, _ ->
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

fun String.sliceList(split: List<Int>, delimiter: String = " ") =
    toList().sliceList(split).joinToString(delimiter) { it.joinToString("") }

fun File.toBase64() = readBytes().base64()
