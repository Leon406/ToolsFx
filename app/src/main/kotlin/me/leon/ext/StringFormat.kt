package me.leon.ext

/**
 * @author Leon
 * @since 2023-02-27 16:27
 * @email deadogone@gmail.com
 */
fun String.center(maxLength: Int, padding: String = " "): String {
    if (length >= maxLength) {
        return this
    }

    val diff = maxLength - length
    val start = diff / 2
    val end = diff - start
    return padding.repeat(start) + this + padding.repeat(end)
}
