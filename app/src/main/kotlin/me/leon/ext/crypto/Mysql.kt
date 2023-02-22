package me.leon.ext.crypto

import me.leon.hash
import me.leon.hash2String

/**
 * Mysql password相关算法 mysqlOld ==> OLD_PASSWORD mysql ==> PASSWORD
 *
 * @author Leon
 * @since 2023-02-08 10:03
 * @email deadogone@gmail.com
 */
private val bypass = charArrayOf(' ', '\t')

fun String.mysqlOld(): String {
    var nr = 1_345_345_333L
    var add: Long = 7
    var nr2 = 0x12_345_671L
    var tmp: Long
    filter { it !in bypass }
        .forEach { char ->
            tmp = char.code.toLong()
            nr = nr xor ((nr and 63L) + add) * tmp + (nr shl 8)
            nr2 += nr2 shl 8 xor nr
            add += tmp
        }

    val part1 = nr and (1L shl 31) - 1L
    val part2 = nr2 and (1L shl 31) - 1L
    return part1.toString(16).padStart(8, '0') + part2.toString(16).padStart(8, '0')
}

fun ByteArray.mysqlOld(): String = decodeToString().mysqlOld()

fun String.mysql() = toByteArray().mysql()

fun ByteArray.mysql() = "*" + hash("SHA").hash2String("SHA")
