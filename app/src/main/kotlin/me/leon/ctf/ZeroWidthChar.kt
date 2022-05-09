package me.leon.ctf

/** ported from https://github.com/yuanfux/zero-width-lib */
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
val indexDict = zeroWidthDict.mapIndexed { index, c -> c to index }.toMap()

fun String.zwc(plain: String) =
    toCharArray()
        .joinToString(zeroWidthDict.last().toString()) {
            it.code.toString(5).map { zeroWidthDict[it - '0'] }.joinToString("")
        }
        .let { "${plain.first()}$it${plain.substring(1)}" }

fun String.zwcDecode() =
    filter { it in zeroWidthDict }
        .split(zeroWidthDict.last())
        .map { it.map { indexDict[it] }.joinToString("").toInt(5).toChar() }
        .joinToString("")
