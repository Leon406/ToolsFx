package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z

fun String.alphabetIndex(
    table: String = TABLE_A_Z,
    delimiter: String = " ",
    fromZero: Boolean = false
) =
    uppercase()
        .asIterable()
        .filter { it in table }
        .map { table.indexOf(it) + (1.takeUnless { fromZero } ?: 0) }
        .joinToString(delimiter)

fun String.alphabetIndexNum(table: String = TABLE_A_Z, fromZero: Boolean = true) =
    uppercase()
        .asIterable()
        .filter { it in table }
        .map { table.indexOf(it) + (1.takeUnless { fromZero } ?: 0) }

fun String.alphabetIndexDecode(table: String = TABLE_A_Z, fromZero: Boolean = false) =
    trim()
        .split("\\D+".toRegex())
        .filter { it.matches("\\d+".toRegex()) }
        .map { table[it.toInt() - (1.takeUnless { fromZero } ?: 0)] }
        .joinToString("")

fun IntArray.alphabetIndexDecodeNum(table: String = TABLE_A_Z, fromZero: Boolean = true) =
    map { table[(26 + it - (1.takeUnless { fromZero } ?: 0)) % 26] }.joinToString("")
