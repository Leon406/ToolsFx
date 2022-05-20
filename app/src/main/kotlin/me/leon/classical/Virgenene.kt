package me.leon.classical

import me.leon.ext.math.circleIndex

fun String.virgeneneEncode(key: String): String {
    var otherCount = 0
    return mapIndexed { index, c ->
            val shift = key[(index - otherCount) % key.length].uppercaseChar() - 'A'
            when (c) {
                in 'A'..'Z' -> 'A' + (c.code - 'A'.code + shift).circleIndex()
                in 'a'..'z' -> 'a' + (c.code - 'a'.code + shift).circleIndex()
                else -> c.also { otherCount++ }
            }
        }
        .joinToString("")
}

fun String.virgeneneDecode(key: String, decryptLength: Int = 0): String {
    var otherCount = 0
    return take(decryptLength.takeIf { it > 0 } ?: length)
        .mapIndexed { index, c ->
            val shift = key[(index - otherCount) % key.length].uppercaseChar() - 'A'
            when (c) {
                in 'A'..'Z' -> 'A' + (c.code - 'A'.code - shift).circleIndex()
                in 'a'..'z' -> 'a' + (c.code - 'a'.code - shift).circleIndex()
                else -> c.also { otherCount++ }
            }
        }
        .joinToString("")
}
