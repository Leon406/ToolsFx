package me.leon.classical

import me.leon.encode.base.padding
import me.leon.ext.crypto.TABLE_A_Z_WO_J
import me.leon.ext.stripAllSpace

fun String.playFair(keyword: String): String {
    val alphabet = TABLE_A_Z_WO_J.toMutableList()
    val key = keyword.replace(" ", "")

    key.uppercase().asIterable().distinct().also {
        alphabet.removeAll(it)
        alphabet.addAll(0, it)
    }
    return replace(" ", "")
        .replace("J", "I")
        .replace("j", "i")
        .replace("(\\w)\\1".toRegex(), "$1X$1")
        .padding("X", 2)
        .chunked(2)
        .joinToString(" ") {
            val char1 = it.first()
            val char2 = it.last()
            val i1 = alphabet.indexOf(char1.uppercaseChar())
            val i2 = alphabet.indexOf(char2.uppercaseChar())
            val tmpChars = mutableListOf<Char>()

            val p1 = i1.point()
            val p2 = i2.point()
            // same row
            if (p1.first == p2.first) {
                tmpChars.add(alphabet[5 * p2.first + (p1.second + 1) % 5].propCase(char2))
                tmpChars.add(alphabet[5 * p1.first + (p2.second + 1) % 5].propCase(char1))
            } else if (p1.second == p2.second) { // same column
                tmpChars.add(alphabet[5 * ((p1.first + 1) % 5) + p2.second].propCase(char1))
                tmpChars.add(alphabet[5 * ((p2.first + 1) % 5) + p1.second].propCase(char2))
            } else {
                tmpChars.add(alphabet[5 * p1.first + p2.second].propCase(char1))
                tmpChars.add(alphabet[5 * p2.first + p1.second].propCase(char2))
            }
            tmpChars.joinToString("")
        }
}

fun Char.propCase(char: Char) = if (char.isLowerCase()) lowercaseChar() else uppercaseChar()

fun String.playFairDecrypt(keyword: String): String {
    val alphabet = TABLE_A_Z_WO_J.toMutableList()
    keyword.replace(" ", "").uppercase().asIterable().distinct().also {
        alphabet.removeAll(it.toSet())
        alphabet.addAll(0, it)
    }
    return stripAllSpace()
        .chunked(2)
        .joinToString("") {
            val char1 = it.first()
            val char2 = it.last()
            val i1 = alphabet.indexOf(char1.uppercaseChar())
            val i2 = alphabet.indexOf(char2.uppercaseChar())
            val tmpChars = mutableListOf<Char>()

            val p1 = i1.point()
            val p2 = i2.point()
            // same row
            if (p1.first == p2.first) {
                tmpChars.add(alphabet[5 * p2.first + (p1.second + 4) % 5].propCase(char2))
                tmpChars.add(alphabet[5 * p1.first + (p2.second + 4) % 5].propCase(char1))
            } else if (p1.second == p2.second) { // same column
                tmpChars.add(alphabet[5 * ((p1.first + 4) % 5) + p2.second].propCase(char1))
                tmpChars.add(alphabet[5 * ((p2.first + 4) % 5) + p1.second].propCase(char2))
            } else {
                tmpChars.add(alphabet[5 * p1.first + p2.second].propCase(char1))
                tmpChars.add(alphabet[5 * p2.first + p1.second].propCase(char2))
            }
            tmpChars.joinToString("")
        }
        .replace("(\\w)X\\1".toRegex(), "$1$1")
}

private fun Int.point(rowNum: Int = 5) = this / rowNum to this % rowNum
