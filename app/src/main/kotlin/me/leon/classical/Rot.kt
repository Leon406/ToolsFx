package me.leon.classical

import me.leon.ext.math.circleIndex

fun String.shift10(bias: Int) =
    this.map { it.takeUnless { it in '0'..'9' } ?: ('0' + (it + bias - '0') % 10) }.joinToString("")

fun String.rot18() =
    this.lowercase()
        .map {
            it.takeUnless { it in '0'..'9' || it in 'a'..'z' }
                ?: with(it) {
                    when (this) {
                        in '0'..'9' -> '0' + (this + 5 - '0') % 10
                        else -> 'a' + (this + 13 - 'a') % 26
                    }
                }
        }
        .joinToString("")

fun String.shift26(bias: Int) =
    this.uppercase()
        .map { it.takeUnless { it in 'A'..'Z' } ?: ('A' + (it + bias - 'A').circleIndex()) }
        .joinToString("")

fun String.shift94(bias: Int) =
    this.map { it.takeUnless { it in '!'..'~' } ?: ('!' + (it + bias - '!').circleIndex(94)) }
        .joinToString("")
