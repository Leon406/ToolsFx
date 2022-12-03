package me.leon.classical

import kotlin.math.ceil
import me.leon.encode.base.padding

/**
 * normal @link https://ctf.bugku.com/tool/railfence type w @link
 * https://en.wikipedia.org/wiki/Rail_fence_cipher https://ctf.bugku.com/tool/railfence
 */

/** count必须为长度的公约数 */
fun String.railFenceEncrypt(count: Int) =
    padding("\r", count)
        .asIterable()
        .mapIndexed { index, c -> index to c }
        .groupBy { it.first % count }
        .values
        .joinToString("") { it.joinToString("") { it.second.toString() } }
        .replace("\r", "")

private fun String.railFenceWithPadding(count: Int): String {
    val block = ceil(length / count.toFloat()).toInt()
    val full = length % count
    val fullIndex = if (full == 0) length else block * full
    return foldIndexed(StringBuilder()) { index, acc, c ->
            acc.apply {
                append(c)
                if (index >= fullIndex && (index - fullIndex + 1) % (block - 1) == 0) {
                    append("\r")
                }
            }
        }
        .toString()
}

/** count必须为长度的公约数 */
fun String.railFenceDecrypt(count: Int) =
    with(railFenceWithPadding(count)) {
        asIterable().chunked(this.length / count).asIterable().run {
            val sb = StringBuilder()
            for (i in (0 until first().size)) {
                sb.append(map { it[i] }.joinToString(""))
            }
            sb.toString().replace("\r", "")
        }
    }
