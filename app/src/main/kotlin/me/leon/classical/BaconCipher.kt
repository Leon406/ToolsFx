package me.leon.classical

/** @link https://wtool.com.cn/baconian.html bacon 26 */
val BACON26 =
    arrayOf(
        "AAAAA",
        "AAAAB",
        "AAABA",
        "AAABB",
        "AABAA",
        "AABAB",
        "AABBA",
        "AABBB",
        "ABAAA",
        "ABAAB",
        "ABABA",
        "ABABB",
        "ABBAA",
        "ABBAB",
        "ABBBA",
        "ABBBB",
        "BAAAA",
        "BAAAB",
        "BAABA",
        "BAABB",
        "BABAA",
        "BABAB",
        "BABBA",
        "BABBB",
        "BBAAA",
        "BBAAB",
    )
val BACON24 =
    arrayOf(
        "AAAAA",
        "AAAAB",
        "AAABA",
        "AAABB",
        "AABAA",
        "AABAB",
        "AABBA",
        "AABBB",
        "ABAAA",
        "ABAAA",
        "ABAAB",
        "ABABA",
        "ABABB",
        "ABBAA",
        "ABBAB",
        "ABBBA",
        "ABBBB",
        "BAAAA",
        "BAAAB",
        "BAABA",
        "BAABB",
        "BAABB",
        "BABAA",
        "BABAB",
        "BABBA",
        "BABBB",
    )

fun String.baconEncrypt24() =
    uppercase()
        .asIterable()
        .map {
            when {
                it.isUpperCase() -> BACON24[it.code - 65]
                else -> it
            }
        }
        .joinToString("")

fun String.baconEncrypt26() =
    uppercase()
        .asIterable()
        .map {
            when {
                it.isUpperCase() -> BACON26[it.code - 65]
                else -> it
            }
        }
        .joinToString("")

fun String.baconDecrypt24(): String {
    val sb = StringBuilder()
    val tmp = StringBuilder()

    for (c in this.uppercase()) {
        when {
            c.isLetter() ->
                if (tmp.length == 4) {
                    tmp.append(c)
                    val cc = 'A' + BACON24.indexOf(tmp.toString())
                    sb.append(cc)
                    tmp.clear()
                } else {
                    tmp.append(c)
                }
            else -> sb.append(c)
        }
    }
    return sb.toString()
}

fun String.baconDecrypt26(): String {
    val sb = StringBuilder()
    val tmp = StringBuilder()

    for (c in this.uppercase()) {
        when {
            c.isLetter() ->
                if (tmp.length == 4) {
                    tmp.append(c)
                    sb.append('A' + BACON26.indexOf(tmp.toString()))
                    tmp.clear()
                } else {
                    tmp.append(c)
                }
            else -> sb.append(c)
        }
    }
    return sb.toString()
}
