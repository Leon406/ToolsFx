package me.leon.classical

import me.leon.ext.math.circleIndex

fun String.affineEncrypt(factor: Int, bias: Int) =
    map {
            when (it) {
                in 'A'..'Z' -> 'A' + (factor * (it - 'A') + bias) % 26
                in 'a'..'z' -> 'a' + (factor * (it - 'a') + bias) % 26
                else -> it
            }
        }
        .joinToString("")

fun String.affineDecrypt(factor: Int, bias: Int) =
    map {
            when (it) {
                in 'A'..'Z' -> 'A' + ((26 - factor) * (it - 'A' - bias)).circleIndex()
                in 'a'..'z' -> 'a' + ((26 - factor) * (it - 'a' - bias)).circleIndex()
                else -> it
            }
        }
        .joinToString("")
