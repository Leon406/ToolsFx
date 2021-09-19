package me.leon.classical

import kotlin.math.floor

/** @link https://en.wikipedia.org/wiki/Rail_fence_cipher */
fun String.railFenceEncrypt(count: Int) =
    String(
        toList().chunked(count).foldIndexed(CharArray(length)) { index, acc, list ->
            acc.apply {
                val d = floor(length / count.toFloat()).toInt()
                for (i in (0 until count)) {
                    val cur = d * i + index
                    println("$i $cur / $length ${list[i]}")
                    if (cur >= length) break

                    acc[cur] = list[i]
                }
            }
        }
    )

fun String.railFenceDecrypt(count: Int) =
    toList()
        .chunked(count)
        .foldIndexed(CharArray(length)) { index, acc, list ->
            acc.apply {
                for (i in (0 until count)) {
                    acc[index * count + i] = list[i]
                }
            }
        }
        .toString()

fun main() {
    println("ATTACKATDAWN".railFenceEncrypt(3))

    println("WECRUOERDSOEERNTNEAIVDAC".railFenceDecrypt(3))
}
