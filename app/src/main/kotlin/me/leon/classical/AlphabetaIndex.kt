package me.leon.classical

fun String.alphabetIndex(
    table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    delimiter: String = " ",
    fromZero: Boolean = false
) =
    uppercase()
        .toCharArray()
        .filter { it in table }
        .map { table.indexOf(it) + (1.takeUnless { fromZero } ?: 0) }
        .joinToString(delimiter)

fun String.alphabetIndexNum(
    table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    fromZero: Boolean = true
) =
    uppercase().toCharArray().filter { it in table }.map {
        table.indexOf(it) + (1.takeUnless { fromZero } ?: 0)
    }

fun String.alphabetIndexDecode(
    table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    fromZero: Boolean = false
) =
    trim()
        .split("[^\\d]+".toRegex())
        .filter { it.matches("\\d+".toRegex()) }
        .map { table[it.toInt() - (1.takeUnless { fromZero } ?: 0)] }
        .joinToString("")

fun IntArray.alphabetIndexDecodeNum(
    table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    fromZero: Boolean = true
) = map { table[(26 + it - (1.takeUnless { fromZero } ?: 0)) % 26] }.joinToString("")
