package me.leon.classical

fun String.alphabetIndex(separator: String = " ") =
    uppercase().toCharArray().filter { it in 'A'..'Z' }.map { it - 'A' + 1 }.joinToString(separator)

fun String.alphabetIndexDecode() =
    trim()
        .split("[^\\d]+".toRegex())
        .filter { it.matches("\\d+".toRegex()) }
        .map { (it.toInt() + 'A'.code - 1).toChar() }
        .joinToString("")
