package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z

/** ported from https://github.com/jameslyons/pycipher/blob/master/pycipher/gronsfeld.py */
fun String.gronsfeld(key: String = "123456"): String {
    val shiftList = key.filter { it.isDigit() }.map { it - '0' }
    return uppercase()
        .filter { it.isUpperCase() }
        .foldIndexed(StringBuilder()) { index, acc, char ->
            acc.append(
                TABLE_A_Z[(TABLE_A_Z.indexOf(char) + shiftList[index % shiftList.size]) % 26]
            )
        }
        .toString()
}

fun String.gronsfeldDecrypt(key: String = "123456"): String {
    val shiftList = key.filter { it.isDigit() }.map { it - '0' }
    return uppercase()
        .filter { it.isUpperCase() }
        .foldIndexed(StringBuilder()) { index, acc, char ->
            acc.append(
                TABLE_A_Z[(TABLE_A_Z.indexOf(char) + 26 - shiftList[index % shiftList.size]) % 26]
            )
        }
        .toString()
}
