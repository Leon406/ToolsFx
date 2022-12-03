package me.leon.classical

import me.leon.ext.splitBySpace
import me.leon.ext.stripAllSpace

// https://en.wikipedia.org/wiki/Morse_code
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

fun String.morseEncrypt() =
    uppercase().stripAllSpace().asIterable().joinToString(" ") {
        DEFAULT_MORSE[it] ?: it.code.toString(2).replace("1", "-").replace("0", ".")
    }

fun String.morseDecrypt(dash: String = "-", dot: String = ".", sep: String = "/") =
    trim()
        .replace(dash, "-")
        .replace(dot, ".")
        .replace(sep, " ")
        .splitBySpace()
        .filterNot { it.isEmpty() }
        .joinToString("") {
            DEFAULT_MORSE_DECODE[it]?.toString()
                ?: it.replace(dash, "1").replace(dot, "0").toInt(2).toChar().toString()
        }
