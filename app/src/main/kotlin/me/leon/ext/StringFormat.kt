package me.leon.ext

/**
 * @author Leon
 * @since 2023-02-27 16:27
 * @email deadogone@gmail.com
 */
fun String.center(maxLength: Int, padding: String = " "): String {
    val len = properLength()
    if (len >= maxLength) {
        return this
    }

    val diff = maxLength - len
    val start = diff / 2
    val end = diff - start
    return padding.repeat(start) + this + padding.repeat(end)
}

fun String.properLength() = map { if (it.code < 128) 1 else 2 }.sum()
