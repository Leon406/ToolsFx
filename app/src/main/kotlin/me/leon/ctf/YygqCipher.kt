package me.leon.ctf

import me.leon.ext.stripAllSpace

private val wordList = arrayOf("就 这 ¿ ", "不 会 吧 ？ ")

fun String.yygq() =
    fold(StringBuilder()) { acc, i ->
            if (i.code < 127) {
                acc.append('0').append(i.code.toString(2).padStart(8, '0'))
            } else {
                acc.append('1').append(i.code.toString(2).padStart(16, '0'))
            }
        }
        .toString()
        .map { wordList[it.toString().toInt()] }
        .joinToString("")

fun String.yygqDecode(): String {
    val binaryString =
        replace((wordList.first() + "*").toRegex(), "0")
            .replace((wordList.last() + "*").toRegex(), "1")
            .stripAllSpace()

    var i = 0
    val sb = StringBuilder()
    while (i < binaryString.length) {
        i +=
            if (binaryString[i] == '0') {
                sb.append(binaryString.substring(i + 1..(i + 8)).toInt(2).toChar())
                8
            } else {
                sb.append(binaryString.substring(i + 1..(i + 16)).toInt(2).toChar())
                16
            }
        i++
    }

    return sb.toString()
}
