package me.leon.classical

import me.leon.ext.*

const val ADFGX_ENCODE_MAP = "ADFGX"

fun String.adfgx(
    table: String,
    keyword: String,
    encodeMap: String = ADFGX_ENCODE_MAP,
    replacePair: Pair<String, String> = "J" to "I"
): String {
    val key = keyword.distinct()
    val polybius = polybius(table, encodeMap, replacePair)
    val keyM =
        key.fold(mutableMapOf<Char, MutableList<String>>()) { acc, c ->
            acc.apply { acc[c] = mutableListOf() }
        }
    return polybius
        .chunked(key.length)
        .fold(keyM) { acc, s ->
            acc.apply { for (i in s.indices) acc[key[i]]!!.add(s[i].toString()) }
        }
        .toSortedMap()
        .values
        .joinToString("") { it.joinToString("") }
}

fun String.adfgxDecrypt(
    table: String,
    keyword: String,
    encodeMap: String = ADFGX_ENCODE_MAP
): String {
    val key = keyword.distinct()
    val sortedKey = key.sorted()
    val count = length % key.length
    val len = length / key.length
    val keyM2: MutableMap<Char, Pair<MutableList<Char>, Int>> =
        key.foldIndexed(mutableMapOf<Char, Pair<MutableList<Char>, Int>>()) { index, acc, c ->
                acc.apply {
                    acc[c] = mutableListOf<Char>() to (len + (if (index < count) 1 else 0))
                }
            }
            .toSortedMap()
    val alphaList = this.toList().sliceList(keyM2.values.map { it.second })
    val listMap = alphaList.associateBy { sortedKey[alphaList.indexOf(it)] }
    // reverse sort
    return chunked(key.length)
        .foldIndexed(CharArray(length)) { i, acc, s ->
            acc.apply {
                s.forEachIndexed { i2, _ -> acc[i * key.length + i2] = listMap[key[i2]]!![i] }
            }
        }
        .joinToString("")
        .polybiusDecrypt(table, encodeMap)
}
