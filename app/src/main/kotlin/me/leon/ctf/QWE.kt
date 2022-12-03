package me.leon.ctf

import me.leon.ext.stripAllSpace

val DEFAULT_QWE =
    mapOf(
        'A' to 'Q',
        'B' to 'W',
        'C' to 'E',
        'D' to 'R',
        'E' to 'T',
        'F' to 'Y',
        'G' to 'U',
        'H' to 'I',
        'I' to 'O',
        'J' to 'P',
        'K' to 'A',
        'L' to 'S',
        'M' to 'D',
        'N' to 'F',
        'O' to 'G',
        'P' to 'H',
        'Q' to 'J',
        'R' to 'K',
        'S' to 'L',
        'T' to 'Z',
        'U' to 'X',
        'V' to 'C',
        'W' to 'V',
        'X' to 'B',
        'Y' to 'N',
        'Z' to 'M',
        'a' to 'q',
        'b' to 'w',
        'c' to 'e',
        'd' to 'r',
        'e' to 't',
        'f' to 'y',
        'g' to 'u',
        'h' to 'i',
        'i' to 'o',
        'j' to 'p',
        'k' to 'a',
        'l' to 's',
        'm' to 'd',
        'n' to 'f',
        'o' to 'g',
        'p' to 'h',
        'q' to 'j',
        'r' to 'k',
        's' to 'l',
        't' to 'z',
        'u' to 'x',
        'v' to 'c',
        'w' to 'v',
        'x' to 'b',
        'y' to 'n',
        'z' to 'm'
    )

val DEFAULT_QWE_DECODE =
    mutableMapOf<Char, Char>().apply { putAll(DEFAULT_QWE.values.zip(DEFAULT_QWE.keys)) }

fun String.qweEncrypt() = stripAllSpace().asIterable().map { DEFAULT_QWE[it] }.joinToString("")

fun String.qweDecrypt() =
    stripAllSpace().asIterable().map { DEFAULT_QWE_DECODE[it] }.joinToString("")
