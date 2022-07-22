package me.leon.ctf

import me.leon.ext.toBinaryString

val ENCODE_8023 = arrayOf("10", "01")
val ENCODE_STANDARD = arrayOf("01", "10")

fun ByteArray.manchester(isStandard: Boolean = false) =
    toBinaryString()
        .map { if (isStandard) ENCODE_STANDARD[(it - '0')] else ENCODE_8023[(it - '0')] }
        .joinToString("")

fun String.manchesterDecode(isStandard: Boolean = false) =
    chunked(2)
        .map { if (isStandard) ENCODE_STANDARD.indexOf(it) else ENCODE_8023.indexOf(it) }
        .joinToString("")

fun ByteArray.manchesterDiff() =
    toBinaryString()
        .foldIndexed(StringBuilder()) { index, acc, c ->
            acc.apply {
                if (index == 0) append(ENCODE_STANDARD[c - '0'])
                else append(if (acc.last() == c) ENCODE_STANDARD[1] else ENCODE_STANDARD[0])
            }
        }
        .toString()

fun String.manchesterDiffDecode() =
    chunked(2)
        .map { ENCODE_STANDARD.indexOf(it) }
        .joinToString("")
        .foldIndexed(StringBuilder()) { index, acc, c ->
            acc.apply {
                if (index == 0) append(c)
                else {
                    val last = this@manchesterDiffDecode[2 * index - 1] - '0'
                    append(if (c == '1') last else (last + 1) % 2)
                }
            }
        }
        .toString()
