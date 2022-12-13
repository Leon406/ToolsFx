package me.leon.encode.base

import me.leon.ext.binary2ByteArray
import me.leon.ext.toBinaryString

/**
 * https://pshihn.github.io/base69/
 *
 * @author Leon
 * @since 2022-09-06 14:09
 */
const val BASE69_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/-*<>|"

private const val CHUNK_SIZE = 7
private const val PAD_AA = "AA"

fun Byte.toBase69Char(dict: String = BASE69_DICT) =
    "${dict.ifEmpty { BASE69_DICT }[this % 69]}${dict.ifEmpty { BASE69_DICT }[this / 69]}"

fun String.base69Char2Byte(dict: String = BASE69_DICT) =
    69 * dict.ifEmpty { BASE69_DICT }.indexOf(last()) +
        dict.ifEmpty { BASE69_DICT }.indexOf(first())

fun ByteArray.base69(dict: String = BASE69_DICT) =
    asIterable().chunked(CHUNK_SIZE).joinToString("") {
        it.toByteArray()
            .toBinaryString()
            .chunked(CHUNK_SIZE) {
                it.toString().padEnd(CHUNK_SIZE, '0').binary2ByteArray().first().toBase69Char(dict)
            }
            .joinToString("")
    } +
        with(CHUNK_SIZE - size % CHUNK_SIZE) {
            if (this > 0) PAD_AA.repeat(this - 1) + "$this=" else ""
        }

fun String.base69(dict: String = BASE69_DICT) = toByteArray().base69(dict)

fun String.base69Decode(dict: String = BASE69_DICT) =
    with(
            chunked(2).joinToString("") {
                if (it.contains("=")) {
                    ""
                } else {
                    it.base69Char2Byte(dict).toString(2).padStart(CHUNK_SIZE, '0')
                }
            }
        ) {
            val replace = replace("(?:00000000)*$".toRegex(), "")
            replace.substring(0, replace.length / BYTE_BITS * BYTE_BITS)
        }
        .binary2ByteArray()

fun String.base69Decode2String(dict: String = BASE69_DICT) = base69Decode(dict).decodeToString()
