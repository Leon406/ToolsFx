package me.leon.classical

fun String.alphabetIndex(table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", separator: String = " ") =
    uppercase()
        .toCharArray()
        .filter { it in table }
        .map { table.indexOf(it) + 1 }
        .joinToString(separator)

fun String.alphabetIndexDecode(table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ") =
    trim()
        .split("[^\\d]+".toRegex())
        .filter { it.matches("\\d+".toRegex()) }
        .map { table[it.toInt() - 1] }
        .joinToString("")
