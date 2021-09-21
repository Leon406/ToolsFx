package me.leon.classical

import kotlin.math.ceil
import kotlin.math.floor

/**
 * normal @link https://ctf.bugku.com/tool/railfence type w @link
 * https://en.wikipedia.org/wiki/Rail_fence_cipher https://ctf.bugku.com/tool/railfence
 */

/** count必须为长度的公约数 */
fun String.railFenceEncrypt(count: Int) =
    String(
        toList().chunked(count).foldIndexed(CharArray(length)) { index, acc, list ->
            acc.apply {
                val d = ceil(length / count.toFloat()).toInt()
                for (i in (0 until count)) {
                    val cur = d * i + index
                    println("$i $cur / $length $list")
                    if (cur >= length) break

                    acc[cur] = list[i]
                }
            }
        }
    )

/** count必须为长度的公约数 */
fun String.railFenceDecrypt(count: Int) =
    toList().chunked(floor(length / count.toFloat()).toInt()).toList().run {
        val sb = StringBuilder()
        for (i in (0 until first().size)) {
            sb.append(map { it[i] }.joinToString(""))
        }
        sb.toString()
    }

fun main() {
    println("ATTACKATDAWN".railFenceEncrypt(6))

    println("ATCADWTAKTAN".railFenceDecrypt(2))
}
