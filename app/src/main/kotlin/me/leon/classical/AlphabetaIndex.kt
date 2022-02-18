package me.leon.classical

fun String.alphabetIndex() =
    uppercase().toCharArray().filter { it in 'A'..'Z' }.map { it - 'A' + 1 }.joinToString(" ")

fun String.alphabetIndexDecode() =
    trim().split("[^\\d]+".toRegex()).map { (it.toInt() + 'A'.code - 1).toChar() }.joinToString("")
