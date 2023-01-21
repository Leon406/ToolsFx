package me.leon.hash

import kotlin.math.ceil
import me.leon.encode.base.padding
import me.leon.ext.binary2ByteArray
import me.leon.ext.toBinaryString
import me.leon.ext.toHex

/**
 * ported from https://github.com/whik/crc-lib-c/blob/master/crcLib.c
 *
 * online https://crccalc.com/ http://www.ip33.com/crc.html
 *
 * crc catalogue https://reveng.sourceforge.io/crc-catalogue/all.htm
 *
 * @author Leon
 * @since 2023-01-17 8:46
 * @email deadogone@gmail.com
 */
fun ByteArray.crc(
    width: Int,
    poly: Long = 0L,
    initial: Long = 0L,
    xorOut: Long = 0L,
    refIn: Boolean = false,
    refOut: Boolean = false
): String = crc(width, poly.toULong(), initial.toULong(), xorOut.toULong(), refIn, refOut)

fun ByteArray.crc(
    width: Int,
    poly: ULong = 0UL,
    initial: ULong = 0UL,
    xorOut: ULong = 0UL,
    refIn: Boolean = false,
    refOut: Boolean = false
): String {
    val mask = if (width == 64) 0xFFFFFFFFFFFFFFFFUL else (1UL shl width) - 1UL
    val half = (1UL shl (width - 1)).coerceAtLeast(0x80UL)
    val diff = (8 - width)
    var crc = (if (diff > 0) initial shl diff else initial).toULong()
    val properPoly = (if (diff > 0) poly shl diff else poly).toULong()
    println(
        "crc ${crc.toString(16)} half ${half.toString(16)}" +
            " mask ${mask.toString(16)} reversePoly ${properPoly.toString(16)}"
    )
    map { it.toULong() }
        .map { if (refIn) it.reflected(8) else it }
        .map { if (diff < 0) it shl -diff else it }
        .forEach { byte ->
            crc = crc xor byte
            if (poly != 0UL) {
                repeat(8) {
                    crc =
                        if (crc and half != 0UL) {
                            (crc shl 1) xor properPoly
                        } else {
                            crc shl 1
                        }
                }
            }
        }

    if (diff > 0) {
        crc = crc shr diff
    }
    if (refOut) {
        crc = crc.reflected(width)
    }

    return (crc xor xorOut and mask).toString(16).properHex(width)
}

private fun String.properHex(width: Int) =
    if (width > 4) padding("0", ceil(width / 4.0).toInt(), false) else this

fun ByteArray.crcReverse(width: Int, poly: Long = 0, initial: Long = 0, xorOut: Long = 0) =
    crcReverse(width, poly.toULong(), initial.toULong(), xorOut.toULong())

fun ByteArray.crcReverse(
    width: Int,
    poly: ULong = 0UL,
    initial: ULong = 0UL,
    xorOut: ULong = 0UL
): String {
    val mask = if (width == 64) 0xFFFFFFFFFFFFFFFFUL else (1UL shl width) - 1UL
    var crc = initial

    val diff = 8 - width
    val reversePoly = if (diff > 0) poly.reflected() shr diff else poly.reflected(width)

    println(
        "crc ${crc.toString(16)} mask ${mask.toString(16)} reversePoly ${reversePoly.toString(16)}"
    )
    map { it.toULong() }
        .forEach { byte ->
            crc = crc xor byte
            repeat(8) {
                crc =
                    if (crc and 1UL == 1UL) {
                        (crc shr 1) xor reversePoly
                    } else {
                        crc shr 1
                    }
            }
        }

    return (crc xor xorOut and mask).toString(16).properHex(width)
}

fun Int.reflected(bits: Int = 8): Int {
    var t = 0
    repeat(bits) { t = t or ((this ushr it and 0x1) shl (bits - 1 - it)) }
    return t
}

fun ULong.reflected(bits: Int = 8): ULong {
    var t = 0UL
    repeat(bits) { t = t or ((this shr it and 0x1UL) shl (bits - 1 - it)) }
    return t
}

fun ByteArray.bitReverse(bitLen: Int = 8): String {
    return toBinaryString()
        .takeLast(bitLen)
        .also { println(it) }
        .reversed()
        .binary2ByteArray()
        .toHex()
}
