package me.leon.classical

import kotlin.math.floor
import me.leon.encode.base.padding

/**
 * normal @link https://ctf.bugku.com/tool/railfence type w @link
 * https://en.wikipedia.org/wiki/Rail_fence_cipher https://ctf.bugku.com/tool/railfence
 */

/** count必须为长度的公约数 */
fun String.railFenceEncrypt(count: Int) =
    padding("@", count)
        .toList()
        .mapIndexed { index, c -> index to c }
        .groupBy { it.first % count }
        .values
        .joinToString("") { it.joinToString("") { it.second.toString() } }

/** count必须为长度的公约数 */
fun String.railFenceDecrypt(count: Int) =
    toList().chunked(floor(length / count.toFloat()).toInt()).toList().run {
        val sb = StringBuilder()
        for (i in (0 until first().size)) {
            sb.append(map { it[i] }.joinToString(""))
        }
        sb.toString().replace("@", "")
    }
