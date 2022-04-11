package me.leon.ctf

import me.leon.ext.splitByNonDigit

fun String.asciiSumDecode(): String =
    with(splitByNonDigit()) {
        dropWhile { indexOf(it) == 0 || indexOf(it) == lastIndex }
            .foldIndexed(StringBuilder()) { index, acc, s ->
                acc.append((s.toInt() - this[index].toInt()).toChar())
            }
            .toString()
    }

fun String.asciiSum(): String {
    var sum = 0
    return fold(StringBuilder("0")) { acc, c ->
            sum += c.code
            acc.append(" ").append(sum)
        }
        .toString()
}
