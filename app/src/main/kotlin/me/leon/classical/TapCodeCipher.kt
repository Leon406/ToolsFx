package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z_WO_K

/** @link http://www.hiencode.com/tapcode.html */
fun String.tapCode(
    table: String = TABLE_A_Z_WO_K,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP
) = polybius(table, encodeMap, "K" to "C")

fun String.tapCodeDecrypt(
    table: String = TABLE_A_Z_WO_K,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP
) = propTapCode().polybiusDecrypt(table, encodeMap)

private fun String.propTapCode() =
    if (contains("[.•]".toRegex())) {
        split("\\s{2,}".toRegex()).joinToString("") {
            val split = it.split("\\s+".toRegex())
            "${split[0].length}${split[1].length}"
        }
    } else {
        this
    }
