package me.leon.classical

import me.leon.ext.letters

/** @link https://zh.wikipedia.org/wiki/%E4%B8%80%E6%AC%A1%E6%80%A7%E5%AF%86%E7%A2%BC%E6%9C%AC */
fun String.oneTimePad(key: String): String {
    val k2 = key.letters().uppercase()
    val d2 = letters().uppercase()
    val isSameSize = k2.length == d2.length
    if (!isSameSize) return this
    return d2.mapIndexed { index, c -> 'A' + (c.code + k2[index].code - 130) % 26 }.joinToString("")
}

fun String.oneTimePadDecrypt(key: String): String {
    val k2 = key.letters().uppercase()
    val d2 = letters().uppercase()
    val isSameSize = k2.length == d2.length
    if (!isSameSize) return this
    return d2.mapIndexed { index, c -> 'A' + (c.code - k2[index].code + 26) % 26 }.joinToString("")
}
