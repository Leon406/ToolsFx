package me.leon

import java.util.concurrent.atomic.AtomicInteger

/**
 * normal @link https://ctf.bugku.com/tool/railfence type w @link
 * https://en.wikipedia.org/wiki/Rail_fence_cipher https://ctf.bugku.com/tool/railfence
 */

/** count必须为长度的公约数 */
fun String.railFenceWEncrypt(count: Int): String {
    val factor = 2 * (count - 1)
    return toList()
        .foldIndexed(mutableMapOf<Int, MutableList<Char>>()) { index, acc, c ->
            acc.apply {
                val propIndex =
                    (index % factor).takeIf { it < count - 1 } ?: (factor - index % factor)
                this[propIndex]?.add(c) ?: kotlin.run { this[propIndex] = mutableListOf(c) }
            }
        }
        .values
        .joinToString("") { it.joinToString("") }
}

/** count必须为长度的公约数 */
fun String.railFenceWDecrypt(count: Int): String {
    val factor = 2 * (count - 1)
    var list = mutableListOf<Pair<MutableList<Char>, Int>>()
    (0 until count).map {
        val len =
            when (it) {
                0, count - 1 -> (this.length - it - 1) / factor + 1
                else ->
                    (this.length - it - 1) / factor +
                        1 +
                        (length + it - factor - 1) / factor +
                        1 +
                        if (length <= (factor - it)) -1 else 0
            }
        list.add(mutableListOf<Char>() to len)
    }
    //    println(list)
    var curList = 0

    this.forEach { c ->
        list[curList].run {
            if (first.size < second) first.add(c) else list[++curList].first.add(c)
        }
    }
    val map = list.map { it.first to AtomicInteger(0) }

    // fill data
    return CharArray(this.length)
        .mapIndexed { index, _ ->
            val listIndex = with(index % factor) { if (this < count - 1) this else factor - this }
            val pair = map[listIndex]
            //        println("$listIndex $index ${pair.first}")
            pair.run { first[second.get()].also { second.getAndIncrement() } }
        }
        .toCharArray()
        .joinToString("")
}
