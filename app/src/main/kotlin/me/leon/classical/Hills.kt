package me.leon.classical

import kotlin.math.sqrt
import me.leon.ext.crypto.TABLE_A_Z
import me.leon.ext.math.*

/** ported from https://github.com/akourk/HillCipher/blob/master/Main.java */
fun String.hillEncrypt(key: String, table: String = TABLE_A_Z, fromZero: Boolean = true): String {
    val keyMatrix = parseKey(key)
    return padEnd(keyMatrix.size, 'x').chunked(keyMatrix.size).joinToString("") {
        keyMatrix
            .multMod(it.alphabetIndexNum(fromZero = fromZero).toIntArray(), table.length)
            .alphabetIndexDecodeNum(table, fromZero = fromZero)
            .lowercase()
    }
}

fun String.hillDecrypt(key: String, table: String = TABLE_A_Z, fromZero: Boolean = true): String {
    val invKey = parseKey(key).invertModMatrix(table.length)
    return padEnd(invKey.size, 'x').chunked(invKey.size).joinToString("") {
        invKey
            .multMod(it.alphabetIndexNum(fromZero = fromZero).toIntArray(), 26)
            .alphabetIndexDecodeNum(table, fromZero = fromZero)
            .lowercase()
    }
}

private fun parseKey(key: String): Array<IntArray> {
    val keyMatrix =
        if (key.contains("\\d+".toRegex())) {
            val m = key.split("\\D+".toRegex()).map { it.toInt() }
            m.reshape(sqrt(m.size.toDouble()).toInt())
        } else {
            val m = key.alphabetIndexNum()
            m.reshape(sqrt(m.size.toDouble()).toInt())
        }
    val determinant = keyMatrix.determinant(keyMatrix.size)
    require(determinant % 26 !in intArrayOf(0, 13) && determinant % 26 % 2 != 0) {
        "wrong key matrix"
    }
    return keyMatrix
}
