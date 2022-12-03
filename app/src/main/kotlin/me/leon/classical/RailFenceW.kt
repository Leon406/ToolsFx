package me.leon.classical

import kotlin.math.abs

/**
 * normal @link https://ctf.bugku.com/tool/railfence type w @link
 * https://en.wikipedia.org/wiki/Rail_fence_cipher https://ctf.bugku.com/tool/railfence
 */
fun String.railFenceWEncrypt(key: Int, offset: Int = 0): String {
    val cycle = 2 * (key - 1)
    return asIterable()
        .foldIndexed(mutableMapOf<Int, MutableList<Char>>()) { pos, acc, c ->
            acc.apply {
                val propIndex = key - 1 - abs(cycle / 2 - (pos + offset) % cycle)
                this[propIndex]?.add(c) ?: kotlin.run { this[propIndex] = mutableListOf(c) }
            }
        }
        .toSortedMap()
        .values
        .joinToString("") { it.joinToString("") }
}

fun String.railFenceWDecrypt(key: Int, offset: Int = 0): String {
    val cycle = 2 * (key - 1)
    val l = Array(length) { '0' }
    var j = 0
    for (y in 0 until key) {
        for (x in indices) {
            if ((y + x + offset) % cycle == 0 || (y - x - offset) % cycle == 0) {
                l[x] = this[j++]
            }
        }
    }
    return l.joinToString("")
}
