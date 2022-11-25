package me.leon.ctf

import me.leon.classical.morseDecrypt
import me.leon.classical.morseEncrypt

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
    toCharArray()
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
