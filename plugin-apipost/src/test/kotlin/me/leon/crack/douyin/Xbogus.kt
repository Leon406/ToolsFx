package me.leon.crack.douyin

import me.leon.ext.readBytesFromNet
import me.leon.hash

/**
 * @author Leon
 * @since 2023-01-12 9:36
 * @email deadogone@gmail.com
 */
const val CHARACTER = "Dkdpgh4ZKsQB80/Mfvw36XI1R25-WUAlEi7NLboqYTOPuzmFjJnryx9HVGcaStCe=" // 固定
const val prefix = "\u0002\u00FF"
val array1 = intArrayOf(216, 130, 1, 201, 52, 71, 7, 172, 222, 114, 97, 177, 88, 101, 108, 14)
val array2 = intArrayOf(89, 173, 178, 78, 243, 205, 190, 2, 151, 240, 91, 57, 88, 39, 69, 63)

fun main() {
    val uid = "MS4wLjABAAAAQf9alelOm8_s-ODwrxGOtZUAl6g8Yss2oHlrQZi8_EA"
    val maxCursor = 1_667_207_880_000L
    val count = 10
    var query = "aid=6383&sec_user_id=$uid&max_cursor=$maxCursor&count=$count"
    val bogus = makeBogus(query)
    println(bogus)
    query = "$query&X-Bogus=$bogus"
    println(query)
    "https://www.douyin.com/aweme/v1/web/aweme/post/?$query"
        .readBytesFromNet(
            headers =
            mapOf(
                "referer" to "https://www.douyin.com/user",
                "user-agent" to
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/104.0.0.0 Safari/537.36",
                "cookie" to
                        "strategyABtestKey=%221672340505.13%22;" +
                        "ttwid=1%7CmKYQriuiDg_yUyQYU8SsO0BLRWWHot8TjPqs3zUWUsU%7C1673312168%7Cd2898f5fb3" +
                        "0808177f883755ee3e61cf955726cc599aaef1c2574ae264160302;"
            )
        )
        .also { println("______" + it.decodeToString()) }
}

fun makeBogus(query: String): String {
    val urlPathArray = query.toByteArray().hash().hash().map { it.toUByte().toInt() }
    val newArray =
        mutableListOf(
            0,
            0,
            1,
            8,
            urlPathArray[14],
            urlPathArray[15],
            array2[14],
            array2[15],
            array1[14],
            array1[15],
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
        )

    newArray.drop(1).fold(newArray.first()) { acc, i -> acc xor i }.also { newArray.add(it) }
    val toByteArray = newArray.map { it.toByte() }.toByteArray()

    val string = toByteArray.toString(Charsets.ISO_8859_1)
    val sss = prefix + encoding_conversion2(b = string)
    return sss.chunked(3).fold(StringBuilder()) { acc, s -> acc.append(calc(s)) }.toString()
}

fun calc(s: String): String {
    require(s.length % 3 == 0)
    val x1 = s[0].code and 255 shl 16
    val x2 = s[1].code and 255 shl 8
    val x3 = x1 or x2 or s[2].code
    return CHARACTER[x3 and 16_515_072 shr 18].toString() +
            CHARACTER[x3 and 258_048 shr 12] +
            CHARACTER[x3 and 4_032 shr 6] +
            CHARACTER[x3 and 63]
}

fun encoding_conversion2(a: String = "ÿ", b: String): String {
    val e = Array(256) { it }
    var d = 0
    var t = ""
    for (r in 0..255) {
        d = (d + e[r] + a[r % a.length].code) % 256
        e[r] = e[d].also { e[d] = e[r] }
    }

    d = 0
    var n = 0
    for (element in b) {
        n = (n + 1) % 256
        d = (d + e[n]) % 256

        e[n] = e[d].also { e[d] = e[n] }
        t += (element.code xor e[(e[n] + e[d]) % 256]).toChar()
    }
    return t
}
