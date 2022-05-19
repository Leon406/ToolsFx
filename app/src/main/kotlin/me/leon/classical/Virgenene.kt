package me.leon.classical

import me.leon.ext.math.circleIndex

fun String.virgeneneEncode(key: String) =
    mapIndexed { index, c ->
            val shift = key[index % key.length].uppercaseChar() - 'A'
            when (c) {
                in 'A'..'Z' -> 'A' + (c.code - 'A'.code + shift).circleIndex()
                in 'a'..'z' -> 'a' + (c.code - 'a'.code + shift).circleIndex()
                else -> c
            }
        }
        .joinToString("")

fun String.virgeneneDecode(key: String, decryptLength: Int = 0) =
    take(decryptLength.takeIf { it > 0 } ?: length)
        .mapIndexed { index, c ->
            val shift = key[index % key.length].uppercaseChar() - 'A'
            when (c) {
                in 'A'..'Z' -> 'A' + (c.code - 'A'.code - shift).circleIndex()
                in 'a'..'z' -> 'a' + (c.code - 'a'.code - shift).circleIndex()
                else -> c
            }
        }
        .joinToString("")
