package me.leon.classical

fun String.caesar(bias: Int) = shift26(bias)

fun String.caesar25() =
    (1..25).joinToString(System.lineSeparator()) { "shift%02d: %s".format(it, caesar(it)) }
