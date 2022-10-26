package me.leon.classical

import me.leon.ext.*

val ENCODE_8023 = arrayOf("10", "01")
val ENCODE_STANDARD = arrayOf("01", "10")

fun ByteArray.manchester(isStandard: Boolean = false, isReverse: Boolean = false) =
    toBinaryString()
        .binaryReverse(isReverse)
        .map { if (isStandard) ENCODE_STANDARD[(it - '0')] else ENCODE_8023[(it - '0')] }
        .joinToString("")

fun String.manchester(isStandard: Boolean = false, isReverse: Boolean = false) =
    autoDecodeToByteArray().manchester(isStandard, isReverse)

fun String.manchesterDecode(isStandard: Boolean = false, isReverse: Boolean = false) =
    autoDecodeToByteArray(true)
        .toBinaryString()
        .chunked(2)
        .map { if (isStandard) ENCODE_STANDARD.indexOf(it) else ENCODE_8023.indexOf(it) }
        .map { it.coerceAtLeast(0) }
        .joinToString("")
        .binaryReverse(isReverse)

fun ByteArray.manchesterDiff(isReverse: Boolean = false) =
    toBinaryString()
        .binaryReverse(isReverse)
        .foldIndexed(StringBuilder()) { index, acc, c ->
            acc.apply {
                if (index == 0) {
                    append(ENCODE_STANDARD[c - '0'])
                } else {
                    append(if (acc.last() == c) ENCODE_STANDARD[1] else ENCODE_STANDARD[0])
                }
            }
        }
        .toString()

fun String.manchesterDiff(isReverse: Boolean = false) =
    autoDecodeToByteArray().manchesterDiff(isReverse)

fun String.manchesterDiffDecode(isReverse: Boolean = false) =
    autoDecodeToByteArray(true)
        .toBinaryString()
        .chunked(2)
        .map { ENCODE_STANDARD.indexOf(it) }
        .joinToString("")
        .foldIndexed(StringBuilder()) { index, acc, c ->
            acc.apply {
                if (index == 0) {
                    append(c)
                } else {
                    val last = this@manchesterDiffDecode[2 * index - 1] - '0'
                    append(if (c == '1') last else (last + 1) % 2)
                }
            }
        }
        .toString()
        .binaryReverse(isReverse)
