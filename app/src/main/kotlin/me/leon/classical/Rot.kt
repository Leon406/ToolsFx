package me.leon.classical

import me.leon.ext.math.circleIndex

fun String.shift10(bias: Int) =
    this.map { it.takeUnless { it in '0'..'9' } ?: ('0' + (it + bias - '0') % 10) }.joinToString("")

fun String.rot18() =
    map {
            when (it) {
                in '0'..'9' -> '0' + (it + 5 - '0') % 10
                in 'A'..'Z' -> 'A' + (it + 13 - 'A') % 26
                in 'a'..'z' -> 'a' + (it + 13 - 'a') % 26
                else -> it
            }
        }
        .joinToString("")

fun String.shift26(bias: Int, biasLower: Int = bias) =
    map {
            when (it) {
                in 'A'..'Z' -> 'A' + (it + bias - 'A').circleIndex()
                in 'a'..'z' -> 'a' + (it + biasLower - 'a').circleIndex()
                else -> it
            }
        }
        .joinToString("")

fun String.shift94(bias: Int) =
    this.map { it.takeUnless { it in '!'..'~' } ?: ('!' + (it + bias - '!').circleIndex(94)) }
        .joinToString("")
