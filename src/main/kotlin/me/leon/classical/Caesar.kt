package me.leon.classical

fun String.caesar(bias: Int) =
    this.uppercase()
        .map { it.takeUnless { it in 'A'..'Z' } ?: ('A' + (it + bias - 'A') % 26) }
        .joinToString("")

fun String.caesar25() =
    (1..25)
        .mapIndexed { index, i -> "shift%02d: %s".format(i, caesar(i)) }
        .joinToString(System.lineSeparator())
