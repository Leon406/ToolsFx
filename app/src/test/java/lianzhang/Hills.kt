package lianzhang

import kotlin.math.sqrt
import me.leon.classical.alphabetIndexDecodeNum
import me.leon.classical.alphabetIndexNum
import me.leon.ext.TABLE_A_Z

fun String.hillSplit(n: Int) = padEnd(n, 'x').chunked(n)

fun String.hillIndexArray(fromZero: Boolean = true) =
    alphabetIndexNum(fromZero = fromZero).toTypedArray()

fun String.hillIndexSplitArray(n: Int, fromZero: Boolean = true) =
    alphabetIndexNum(fromZero = fromZero).chunked(n).map { it.toIntArray() }.toTypedArray()

fun Array<IntArray>.toCharacter(fromZero: Boolean = true) =
    joinToString("") { it.alphabetIndexDecodeNum(fromZero = fromZero) }.lowercase()

fun String.hillEncrypt(key: String, table: String = TABLE_A_Z): String {
    val keyMatrix = parseKey(key)
    return padEnd(keyMatrix.size, 'x').chunked(keyMatrix.size).joinToString("") {
        keyMatrix
            .multMod(it.alphabetIndexNum().toIntArray(), table.length)
            .alphabetIndexDecodeNum(table)
            .lowercase()
    }
}

fun String.hillDecrypt(key: String, table: String = TABLE_A_Z): String {
    val keyMatrix = parseKey(key).invertModMatrix(table.length)
    return padEnd(keyMatrix.size, 'x').chunked(keyMatrix.size).joinToString("") {
        keyMatrix
            .multMod(it.alphabetIndexNum().toIntArray(), 26)
            .alphabetIndexDecodeNum(table)
            .lowercase()
    }
}

private fun parseKey(key: String): Array<IntArray> {
    val keyMatrix =
        if (key.contains("\\d+".toRegex())) {
            val m = key.split("[^\\d]+".toRegex()).map { it.toInt() }
            m.reshape(sqrt(m.size.toDouble()).toInt())
        } else {
            val m = key.alphabetIndexNum()
            m.reshape(sqrt(m.size.toDouble()).toInt())
        }
    val determinant = keyMatrix.determinant(keyMatrix.size)
    if (determinant % 26 in arrayOf(0, 13) || determinant % 26 % 2 == 0) {
        throw IllegalArgumentException("wrong key matrix")
    }
    return keyMatrix
}
