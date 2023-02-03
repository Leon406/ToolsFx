package me.leon.classical

import me.leon.ext.*
import me.leon.ext.math.circleIndex

/**
 * https://www.jisuan.mobi/vHbummzm1Bb6UXQQ.html
 *
 * @author Leon
 * @since 2023-02-03 10:55
 * @email deadogone@gmail.com
 */
private const val DICT_TYPE7 = "dsfd;kfoA,.iyewrkldJKDHSUBsgvca69834ncxv9873254k;fg87"

fun String.type7(seed: Int = 0) =
    seed.toString().padStart(2, '0') +
        toByteArray()
            .mapIndexed { i, c ->
                val t = (seed + i).circleIndex(53)
                c xor DICT_TYPE7[t].code
            }
            .toByteArray()
            .toHex()

fun String.type7Decode(): String {
    val seed = substring(0, 2).toInt()
    println(seed)
    return drop(2)
        .chunked(2)
        .mapIndexed { i, s ->
            val t = (seed + i).circleIndex(53)
            s.hex2ByteArray()[0] xor DICT_TYPE7[t].code
        }
        .toByteArray()
        .decodeToString()
}
