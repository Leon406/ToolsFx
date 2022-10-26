package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z_WO_J
import me.leon.ext.stripAllSpace

/** @link https://wtool.com.cn/polybius.html */
const val DEFAULT_POLYBIUS_ENCODE_MAP = "12345"

fun String.polybius(
    table: String = TABLE_A_Z_WO_J,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP,
    replacePair: Pair<String, String> = "J" to "I"
): String {
    val properTable = table.stripAllSpace().uppercase()
    val map =
        properTable.associateWith {
            val i = properTable.indexOf(it)
            "${encodeMap[i / encodeMap.length]}${encodeMap[i % encodeMap.length]}"
        }
    return uppercase()
        .replace(replacePair.first, replacePair.second)
        .map { map[it] ?: it }
        .joinToString("")
}

fun String.polybiusDecrypt(
    table: String = TABLE_A_Z_WO_J,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP
): String {
    val properTable = table.stripAllSpace().uppercase()
    val map =
        properTable.associateBy {
            val i = properTable.indexOf(it)
            "${encodeMap[i / encodeMap.length]}${encodeMap[i % encodeMap.length]}"
        }
    val sb = StringBuilder()
    val tmp = StringBuilder()

    for (c in this) {
        when {
            c.isDigit() || c.isLetter() ->
                if (tmp.length == 1) {
                    tmp.append(c)
                    sb.append(map[tmp.toString()] ?: tmp.toString())
                    tmp.clear()
                } else {
                    tmp.append(c)
                }
            else -> sb.append(c)
        }
    }
    return sb.toString()
}
