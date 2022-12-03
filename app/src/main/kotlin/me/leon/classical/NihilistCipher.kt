package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z_WO_J
import me.leon.ext.stripAllSpace

/** like polybius @link https://ctf-wiki.org/crypto/classical/polyalphabetic/#nihilist */
fun String.nihilist(
    keyword: String,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP,
    replacePair: Pair<String, String> = "J" to "I"
): String {
    val maps = TABLE_A_Z_WO_J.toMutableList()
    keyword.stripAllSpace().uppercase().asIterable().distinct().also {
        maps.removeAll(it.toSet())
        maps.addAll(0, it)
    }
    return polybius(maps.joinToString(""), encodeMap, replacePair)
}

fun String.nihilistDecrypt(
    keyword: String,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP
): String {
    val maps = TABLE_A_Z_WO_J.toMutableList()
    keyword.stripAllSpace().uppercase().asIterable().distinct().also {
        maps.removeAll(it.toSet())
        maps.addAll(0, it)
    }
    return polybiusDecrypt(maps.joinToString(""), encodeMap)
}
