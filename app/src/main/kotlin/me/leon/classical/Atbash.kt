package me.leon.classical

/** @link https://en.wikipedia.org/wiki/Atbash */
fun String.atBash() =
    map {
            when (it) {
                in 'A'..'Z' -> 'A' + (25 * (it - 'A') + 25) % 26
                in 'a'..'z' -> 'a' + (25 * (it - 'a') + 25) % 26
                else -> it
            }
        }
        .joinToString("")
