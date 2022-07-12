package me.leon.ext

inline fun <reified T> Any?.safeAs(): T? = this as? T

inline fun <reified T> Any?.cast() = this as T

// Int 4个字节
fun String.unicodeCharToInt() =
    toByteArray(Charsets.UTF_32BE).fold(0) { acc, b -> acc * 256 + b.toInt().and(0xFF) }

fun Int.toUnicodeChar(): String =
    takeIf { it < 65_536 }?.toChar()?.toString()
        ?: toBigInteger().toByteArray().padStart(4, 0x00).toString(Charsets.UTF_32BE)

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

fun String.containsRegexIgnoreCase(keyword: String) = contains("(?i)$keyword".toRegex())
