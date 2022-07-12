package me.leon.ctf

import kotlin.math.ceil
import me.leon.encode.base.padding

private const val PADDING_CHAR = 0.toChar()

fun String.caesarBox(height: Int): String {
    val properData = replace(" ", "")
    val width = ceil(properData.length / height.toDouble()).toInt()
    val input = properData.padding(PADDING_CHAR.toString(), width)
    val sb = StringBuilder()
    for (i in 0 until height) {
        for (j in i until input.length step height) {
            if (input[j] != PADDING_CHAR) {
                sb.append(input[j])
            }
        }
    }
    return sb.toString()
}

fun String.caesarBoxDecrypt(height: Int): String {
    return caesarBox(ceil(length / height.toDouble()).toInt())
}
