package me.leon.classical

import me.leon.ext.binary2Ascii
import me.leon.ext.toBinaryString

fun String.grayEncode(length: Int = 0, delimiter: String = " "): String {
    val binary = toBinaryString()
    val len = length.takeIf { it > 0 } ?: binary.length
    return binary.chunked(len).joinToString(delimiter) {
        it.foldIndexed(StringBuilder()) { index, acc, c ->
                acc.apply {
                    if (index == 0) {
                        acc.append(c)
                    } else {
                        acc.append((c - '0').xor(it[index - 1] - '0'))
                    }
                }
            }
            .toString()
    }
}

fun String.grayDecode(length: Int = 0, delimiter: String = " "): String {
    val binary = replace(delimiter, "").replace("[^01]".toRegex(), "")
    val len = length.takeIf { it > 0 } ?: binary.length
    return binary
        .chunked(len)
        .joinToString("") {
            it.foldIndexed(StringBuilder()) { index, acc, c ->
                    acc.apply {
                        if (index == 0) {
                            acc.append(c)
                        } else {
                            acc.append((c - '0').xor(acc.last() - '0'))
                        }
                    }
                }
                .toString()
        }
        .binary2Ascii()
}
