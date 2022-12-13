package me.leon.ctf

/**
 * @author Leon
 * @since 2022-10-21 14:08
 */

/** A-Z */
val FENHAM_DICT =
    arrayOf(
        "1000001",
        "1000010",
        "1000011",
        "1000100",
        "1000101",
        "1000110",
        "1000111",
        "1001000",
        "1001001",
        "1001010",
        "1001011",
        "1001100",
        "1001101",
        "1001110",
        "1001111",
        "1010000",
        "1010001",
        "1010010",
        "1010011",
        "1010100",
        "1010101",
        "1010110",
        "1010111",
        "1011000",
        "1011001",
        "1011010"
    )

fun String.fenham(key: String): String {
    require(length == key.length) { "key length must equal data" }
    return uppercase()
        .filter { it.isUpperCase() }
        .mapIndexed { index, char ->
            FENHAM_DICT[char - 'A'].xor(FENHAM_DICT[key.uppercase()[index] - 'A'])
        }
        .joinToString("")
}

fun String.fenhamDecrypt(key: String): String {
    require(length % 7 == 0)
    require(length / 7 == key.length) { "key length must equal data" }
    return chunked(7)
        .mapIndexed { index, char ->
            'a' + FENHAM_DICT.indexOf(char.xor(FENHAM_DICT[key.uppercase()[index] - 'A']))
        }
        .joinToString("")
}

fun String.xor(another: String) =
    foldIndexed(StringBuilder()) { index, acc, c ->
            acc.append(if (c == another[index]) "0" else "1")
        }
        .toString()
