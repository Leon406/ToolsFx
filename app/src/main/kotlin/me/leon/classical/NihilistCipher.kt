package me.leon.classical

/** like polybius  @link https://ctf-wiki.org/crypto/classical/polyalphabetic/#nihilist */

fun String.nihilist(
    keyword: String,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP,
    replacePair: Pair<String, String> = "J" to "I"
): String {
    val maps = "ABCDEFGHIKLMNOPQRSTUVWXYZ".toMutableList()
    keyword.replace(" ", "").uppercase().toList().distinct().also {
        maps.removeAll(it)
        maps.addAll(0, it)
    }
    return polybius(maps.joinToString(""), encodeMap, replacePair)
}

fun String.nihilistDecrypt(
    keyword: String,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP
): String {
    val maps = "ABCDEFGHIKLMNOPQRSTUVWXYZ".toMutableList()
    keyword.replace(" ", "").uppercase().toList().distinct().also {
        maps.removeAll(it)
        maps.addAll(0, it)
    }
    return polybiusDecrypt(maps.joinToString(""), encodeMap)
}
