package me.leon.classical

fun String.alphabetIndex(table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", delimiter: String = " ") =
    uppercase()
        .toCharArray()
        .filter { it in table }
        .map { table.indexOf(it) + 1 }
        .joinToString(delimiter)

fun String.alphabetIndexDecode(table: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ") =
    trim()
        .split("[^\\d]+".toRegex())
        .filter { it.matches("\\d+".toRegex()) }
        .map { table[it.toInt() - 1] }
        .joinToString("")
