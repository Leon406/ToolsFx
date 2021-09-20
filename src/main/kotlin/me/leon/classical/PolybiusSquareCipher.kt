package me.leon.classical

/**
 * @link https://wtool.com.cn/polybius.html
 */
const val DEFAULT_POLYBIUS_TABLE = "ABCDEFGHIKLMNOPQRSTUVWXYZ"
const val DEFAULT_POLYBIUS_ENCODE_MAP = "12345"

fun String.polybius(table: String = DEFAULT_POLYBIUS_TABLE, encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP): String {
    val map = table.associateWith {
        val i = table.indexOf(it)
        "${encodeMap[i / 5]}${encodeMap[i % 5]}"
    }
    return uppercase().replace("J", "I").map { map[it] ?: it }.joinToString("")
}

fun String.polybiusDecrypt(
    table: String = DEFAULT_POLYBIUS_TABLE,
    encodeMap: String = DEFAULT_POLYBIUS_ENCODE_MAP
): String {
    val map = table.associateBy {
        val i = table.indexOf(it)
        "${encodeMap[i / 5]}${encodeMap[i % 5]}"
    }
    val sb = StringBuilder()
    val tmp = StringBuilder()

    for (c in this) {
        when {
            c.isDigit() -> if (tmp.length == 1) {
                tmp.append(c)
                sb.append(map[tmp.toString()] ?: tmp.toString())
                tmp.clear()
            } else tmp.append(c)
            else -> sb.append(c)
        }
    }

    return sb.toString()
}
