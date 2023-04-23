package me.leon.ctf

import me.leon.REG_NON_PRINTABLE
import me.leon.classical.hackWordDecode
import me.leon.ctf.Words.isWord
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode2String
import me.leon.ext.crypto.next

/**
 * @author Leon
 * @since 2023-04-23 16:29
 * @email deadogone@gmail.com
 */
val DEFAULT_CONDITION = { s: String ->
    with(s.base64Decode2String()) {
        all { it.code < 128 && it.code != 10 && it.code != 13 } &&
            !REG_NON_PRINTABLE.containsMatchIn(this)
    }
}

fun String.base64CaseCrack(words: String = ""): String {
    if (words.isNotEmpty()) {
        Words.DICT_WORDS.addAll(words.tokenize())
    }
    return chunked(4)
        .map { it.caseEnum() }
        .enums()
        .map { it to it.base64Decode2String() }
        .filter { it.second.hackWordDecode().tokenize().all { it.isWord() } }
        .joinToString(System.lineSeparator()) { "${it.second} ${it.first}" }
}

fun String.base64UpperCase() = base64().uppercase()

fun String.caseEnum(cond: (s: String) -> Boolean = DEFAULT_CONDITION) =
    fold(sequenceOf("")) { acc, c ->
            val dic =
                when {
                    c.isUpperCase() -> "$c${c.lowercase()}"
                    c.isLowerCase() -> "$c${c.uppercase()}"
                    else -> c.toString()
                }
            acc.next(dic)
        }
        .filter(cond)
        .toList()

fun List<List<String>>.enums(): List<String> {
    require(isNotEmpty())
    var r = reduceSize()
    while (r.size != 1) {
        r = r.reduceSize()
    }
    return r[0]
}

private fun List<List<String>>.reduceSize(): List<List<String>> {
    require(isNotEmpty())
    if (size == 1) return this
    val r = subList(1, size).toMutableList()
    r[0] = this[0].flatMap { f -> this[1].map { "$f$it" } }
    return r
}

fun String.tokenize() =
    lowercase().split("[^a-zA-Z]".toRegex()).distinct().filter { it.isNotEmpty() }
