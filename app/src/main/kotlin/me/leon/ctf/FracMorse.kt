package me.leon.ctf

import me.leon.classical.DEFAULT_MORSE
import me.leon.classical.DEFAULT_MORSE_DECODE

/**
 * ported from https://programtalk.com/vs2/?source=python/5968/pycipher/pycipher/fracmorse.py
 *
 * @author Leon
 * @since 2022-10-21 15:07
 */
val FRAC_DICT =
    listOf(
        "...",
        "..-",
        "..x",
        ".-.",
        ".--",
        ".-x",
        ".x.",
        ".x-",
        ".xx",
        "-..",
        "-.-",
        "-.x",
        "--.",
        "---",
        "--x",
        "-x.",
        "-x-",
        "-xx",
        "x..",
        "x.-",
        "x.x",
        "x-.",
        "x--",
        "x-x",
        "xx.",
        "xx-"
    )
val MORSE_REG = "[^A-Z0-9 (,.:')\\-/;\\\\?_]".toRegex()
val MORSE_SPACE_REG = " +".toRegex()
val MORSE_END_SPACE_REG = " *\$".toRegex()

// hello ROUNDTABLECFGHIJKMPQSVWXYZ RAQUNBI
fun String.fracMorse(key: String): String {
    require(key.length == 26) { "key length must be 26" }
    val mapping = FRAC_DICT.zip(key.asIterable()).toMap()
    return uppercase()
        .replace(MORSE_REG, "")
        .replace(MORSE_SPACE_REG, " ")
        .replace(MORSE_END_SPACE_REG, "")
        .map { DEFAULT_MORSE[it] + 'x' }
        .joinToString("")
        .chunked(3)
        .fold(StringBuilder()) { acc, s ->
            acc.apply {
                when (s.length % 3) {
                    1 -> append("")
                    2 -> append(mapping[s + 'x'])
                    else -> append(mapping[s])
                }
            }
        }
        .toString()
}

fun String.fracMorseDecrypt(key: String): String {
    require(key.length == 26) { "key length must be 26" }
    val reverseMapping = key.asIterable().zip(FRAC_DICT).toMap()
    return map { reverseMapping[it] }
        .joinToString("")
        .split("x")
        .filter { it.isNotEmpty() }
        .joinToString("") { DEFAULT_MORSE_DECODE[it].toString() }
        .lowercase()
}
