package me.leon.misc

/**
 * @author Leon
 * @since 2023-05-25 14:13
 * @email deadogone@gmail.com
 */
private val ROMAN_DECODE_DICT =
    mapOf(
        'I' to 1,
        'V' to 5,
        'X' to 10,
        'L' to 50,
        'C' to 100,
        'D' to 500,
        'M' to 1000,
    )
private val ROMAN_DICT =
    arrayOf(
        arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"),
        arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"),
        arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"),
        arrayOf("", "M", "MM", "MMM")
    )

fun Int.toRoman(): String {
    require(this in 1..3999) { "number must in 1..3999" }
    return buildString {
        append(ROMAN_DICT[3][this@toRoman / 1000 % 10])
        append(ROMAN_DICT[2][this@toRoman / 100 % 10])
        append(ROMAN_DICT[1][this@toRoman / 10 % 10])
        append(ROMAN_DICT[0][this@toRoman % 10])
    }
}

fun String.romanToInt(): Int {
    return foldIndexed(0) { index, acc, c ->
        val v = ROMAN_DECODE_DICT[c]!!
        if (index < lastIndex && v < ROMAN_DECODE_DICT[this[index + 1]]!!) {
            acc - v
        } else {
            acc + v
        }
    }
}

fun String.roman() =
    if (matches("\\d+".toRegex())) {
        toInt().toRoman()
    } else {
        romanToInt().toString()
    }
