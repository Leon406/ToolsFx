package me.leon.classical

/** @link https://en.wikipedia.org/wiki/Atbash */
fun String.atBash() =
    this.uppercase()
        .map { it.takeUnless { it in 'A'..'Z' } ?: ('A' + (25 * (it - 'A') + 25) % 26) }
        .joinToString("")
