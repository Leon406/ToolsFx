package me.leon.classical

/**
 * @author Leon
 * @since 2023-04-23 11:50
 * @email deadogone@gmail.com
 */
private val HACKER_DICT =
    mapOf(
        "A" to "4",
        "E" to "3",
        "I" to "1",
        "O" to "0",
        "S" to "5",
        "T" to "7",
    )

private val HACKER_DICT_REVERSE = HACKER_DICT.values.zip(HACKER_DICT.keys).toMap()

fun String.hackWord() = uppercase().map { HACKER_DICT[it.toString()] ?: it }.joinToString("")

fun String.hackWordDecode() =
    uppercase().map { HACKER_DICT_REVERSE[it.toString()] ?: it }.joinToString("").lowercase()
