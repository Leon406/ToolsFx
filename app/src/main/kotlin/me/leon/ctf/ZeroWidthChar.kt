package me.leon.ctf

import kotlin.math.ceil
import kotlin.math.log
import me.leon.classical.morseDecrypt
import me.leon.classical.morseEncrypt
import me.leon.ext.*

// ported from https://github.com/yuanfux/zero-width-lib
// ported from https://github.com/rover95/morse-encrypt

// \u200c
private const val ZERO_WIDTH_NON_JOINER = '‌'

// \u200d
private const val ZERO_WIDTH_JOINER = '‍'

// \u200b
private const val ZERO_WIDTH_SPACE = '​'

// \ufeff
private const val ZERO_WIDTH_NO_BREAK_SPACE = '﻿'

// \u200e
private const val LEFT_TO_RIGHT_MARK = '‎'

// \u200f
private const val RIGHT_TO_LEFT_MARK = '‏'

val zeroWidthDict =
    listOf(
        LEFT_TO_RIGHT_MARK,
        RIGHT_TO_LEFT_MARK,
        ZERO_WIDTH_NON_JOINER,
        ZERO_WIDTH_JOINER,
        ZERO_WIDTH_NO_BREAK_SPACE,
        ZERO_WIDTH_SPACE
    )

val zeroWidthThreeDict =
    listOf(
        // 0
        ZERO_WIDTH_NON_JOINER,
        // 1
        ZERO_WIDTH_SPACE,
        // split
        ZERO_WIDTH_JOINER
    )

val indexDict = zeroWidthDict.mapIndexed { index, c -> c to index }.toMap()

fun String.zwcBinary(plain: String) =
    asIterable()
        .joinToString(zeroWidthDict.last().toString()) {
            it.code.toString(5).map { zeroWidthDict[it - '0'] }.joinToString("")
        }
        .run { "${plain.first()}$this${plain.substring(1)}" }

fun String.zwcBinaryDecode() =
    filter { it in zeroWidthDict }
        .split(zeroWidthDict.last())
        .filterNot { it.isEmpty() }
        .map { it.map { indexDict[it] }.joinToString("").toInt(5).toChar() }
        .joinToString("")

fun String.zwcMorseDecode() =
    filter { it in zeroWidthThreeDict }
        .replace(ZERO_WIDTH_NON_JOINER.toString(), ".")
        .replace(ZERO_WIDTH_JOINER.toString(), "-")
        .replace(ZERO_WIDTH_SPACE.toString(), " ")
        .morseDecrypt()
        .lowercase()

fun String.zwcMorse(plain: String) =
    morseEncrypt()
        .replace(".", ZERO_WIDTH_NON_JOINER.toString())
        .replace("-", ZERO_WIDTH_JOINER.toString())
        .replace(" ", ZERO_WIDTH_SPACE.toString())
        .run { "${plain.first()}$this${plain.substring(1)}" }

const val ZWC_UNICODE_DICT = "\\u200c\\u200d\\u202c\\ufeff"

fun String.zwcUnicode(show: String, dict: String = ZWC_UNICODE_DICT): String {
    val encodeMap = dict.unicode2String()
    val radix = encodeMap.length
    val encodeLength = ceil(log(65_536.0, radix.toDouble())).toInt()
    return map {
            it.code
                .toString(radix)
                .padStart(encodeLength, '0')
                .map { encodeMap[it - '0'] }
                .joinToString("")
        }
        .joinToString("")
        .run { "${show.first()}$this${show.substring(1)}" }
}

fun String.zwcUnicodeDecode(dict: String = ZWC_UNICODE_DICT): String {
    val encodeMap = parseDict(dict)
    val radix = encodeMap.length
    val encodeLength = ceil(log(65_536.0, radix.toDouble())).toInt()

    return filter { it in encodeMap }
        .chunked(encodeLength)
        .map { it.map { encodeMap.indexOf(it) }.joinToString("").toInt(radix).toChar() }
        .joinToString("")
}

fun String.zwcUnicodeBinary(show: String, dict: String = ZWC_UNICODE_DICT): String {
    val encodeMap = dict.unicode2String()
    val radix = encodeMap.length
    val encodeLength = ceil(log(256.0, radix.toDouble())).toInt()

    return toByteArray()
        .joinToString("") {
            it.toString(radix)
                .padStart(encodeLength, '0')
                .map { encodeMap[it - '0'] }
                .joinToString("")
        }
        .run { "${show.first()}$this${show.substring(1)}" }
}

fun String.zwcUnicodeDecodeBinary(dict: String = ZWC_UNICODE_DICT): String {
    val encodeMap = parseDict(dict)
    val radix = encodeMap.length
    val encodeLength = ceil(log(256.0, radix.toDouble())).toInt()
    return zwcUnicodeDecode2Radix(encodeMap)
        .chunked(encodeLength)
        .map { it.toByte(encodeLength) }
        .toByteArray()
        .decodeToString()
}

private fun String.parseDict(dict: String): String {
    var encodeMap = dict.unicode2String()
    val zwcMapInData =
        distinct().sorted().filter {
            val unicode = it.toString().toUnicodeString()
            unicode.startsWith("\\u20") || unicode == "\\ufeff"
        }

    println(zwcMapInData.toUnicodeString())

    if (dict == ZWC_UNICODE_DICT && zwcMapInData.any { !encodeMap.contains(it) }) {
        encodeMap = zwcMapInData
    }
    return encodeMap
}

private fun String.zwcUnicodeDecode2Radix(encodeMap: String): String {
    return filter { it in encodeMap }.map { encodeMap.indexOf(it) }.joinToString("")
}
