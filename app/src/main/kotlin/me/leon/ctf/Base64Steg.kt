package me.leon.ctf

import me.leon.encode.base.BASE64_DICT
import me.leon.encode.base.padding
import me.leon.ext.binary2Ascii

/**
 * https://blog.csdn.net/Sanctuary1307/article/details/113836907
 * @author Leon
 * @since 2022-12-06 10:14
 * @email: deadogone@gmail.com
 */
fun String.base64Steg() =
    lines()
        .joinToString("") { it.base64LineSteg() }
        .padding("0", 8)
        .chunked(8)
        .filter { it != "00000000" }
        .joinToString("")
        .binary2Ascii()

private fun String.base64LineSteg() =
    with(chunked(4).takeLast(1).single()) {
        val count = count { it == '=' }
        if (count > 0) {
            BASE64_DICT.indexOf(this[3 - count])
                .toString(2)
                .padding("0", 6, false)
                .takeLast(count * 2)
        } else {
            ""
        }
    }
