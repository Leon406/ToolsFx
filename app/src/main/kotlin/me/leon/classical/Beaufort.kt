package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z

/** ported from https://github.com/jameslyons/pycipher/blob/master/pycipher/beaufort.py */
fun String.beaufort(key: String = "FORTIFICATION"): String {
    val properKey = key.uppercase().filter { it.isUpperCase() }
    return uppercase()
        .filter { it.isUpperCase() }
        .foldIndexed(StringBuilder()) { index, acc, char ->
            acc.append(
                TABLE_A_Z[
                    (TABLE_A_Z.indexOf(properKey[index % properKey.length]) -
                        TABLE_A_Z.indexOf(char) + 26) % 26]
            )
        }
        .toString()
}
