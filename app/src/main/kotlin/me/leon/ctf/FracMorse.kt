package me.leon.ctf

/**
 *
 * ported from https://programtalk.com/vs2/?source=python/5968/pycipher/pycipher/fracmorse.py
 * @author Leon
 * @since 2022-10-21 15:07
 * @email: deadogone@gmail.com
 */
val DEFAULT_MORSE =
    mapOf(
        'A' to ".-",
        'B' to "-...",
        'C' to "-.-.",
        'D' to "-..",
        'E' to ".",
        'F' to "..-.",
        'G' to "--.",
        'H' to "....",
        'I' to "..",
        'J' to ".---",
        'K' to "-.-",
        'L' to ".-..",
        'M' to "--",
        'N' to "-.",
        'O' to "---",
        'P' to ".--.",
        'Q' to "--.-",
        'R' to ".-.",
        'S' to "...",
        'T' to "-",
        'U' to "..-",
        'V' to "...-",
        'W' to ".--",
        'X' to "-..-",
        'Y' to "-.--",
        'Z' to "--..",
        '1' to ".----",
        '2' to "..---",
        '3' to "...--",
        '4' to "....-",
        '5' to ".....",
        '6' to "-....",
        '7' to "--...",
        '8' to "---..",
        '9' to "----.",
        '0' to "-----",
        '.' to ".-.-.-",
        ':' to "---...",
        ',' to "--..--",
        ';' to "-.-.-.",
        '?' to "..--..",
        '=' to "-...-",
        '\'' to ".----.",
        '/' to "-..-.",
        '!' to "-.-.--",
        '-' to "-....-",
        '+' to ".-.-.",
        '_' to "..--.-",
        '"' to ".-..-.",
        '(' to "-.--.",
        ')' to "-.--.-",
        '$' to "...-..-",
        '&' to ".-...",
        '@' to ".--.-.",
    )

val DEFAULT_MORSE_DECODE =
    mutableMapOf<String, Char>().apply { putAll(DEFAULT_MORSE.values.zip(DEFAULT_MORSE.keys)) }

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
    val mapping = FRAC_DICT.zip(key.toList()).toMap()
    println(this)
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
    val reverseMapping = key.toList().zip(FRAC_DICT).toMap()
    return map { reverseMapping[it] }
        .joinToString("")
        .split("x")
        .filter { it.isNotEmpty() }
        .joinToString("") { DEFAULT_MORSE_DECODE[it].toString() }
        .lowercase()
}
