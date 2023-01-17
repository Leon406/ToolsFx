package me.leon.hash

import kotlin.math.ceil
import me.leon.encode.base.padding

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
    poly: Int = 0,
    initial: Int = 0x00,
    xorOut: Int = 0x00,
    refIn: Boolean = false,
    refOut: Boolean = false
): String {
    val mask = (1 shl width) - 1
    val half = (1 shl (width - 1)).coerceAtLeast(0x80)
    val diff = (8 - width)
    var crc = if (diff > 0) initial shl diff else initial
    val properPoly = if (diff > 0) poly shl diff else poly

    map { if (refIn) it.toInt().reflected(8) else it.toInt() }
        .map { if (diff < 0) it shl -diff else it }
        .forEach { byte ->
            crc = crc xor byte
            if (poly != 0) {
                repeat(8) {
                    crc =
                        if (crc and half != 0) {
                            (crc shl 1) xor properPoly
                        } else {
                            crc shl 1
                        }
                }
            }
        }

    if (diff > 0) {
        crc = crc ushr diff
    }
    if (refOut) {
        crc = crc.reflected(width)
    }

    return (crc xor xorOut and mask).toString(16).properHex(width)
}

fun ByteArray.crc(
    width: Int,
    poly: Long = 0L,
    initial: Long = 0L,
    xorOut: Long = 0L,
    refIn: Boolean = false,
    refOut: Boolean = false
): String {
    val mask = ((1L shl width) - 1).toULong()
    val half = ((1L shl (width - 1)).coerceAtLeast(0x80L)).toULong()
    val diff = (8 - width)
    var crc = (if (diff > 0) initial shl diff else initial).toULong()
    val properPoly = (if (diff > 0) poly shl diff else poly).toULong()
    val zero = 0.toULong()

    map { it.toULong() }
        .map { if (refIn) it.toLong().reflected(8).toULong() else it }
        .map { if (diff < 0) it shl -diff else it }
        .forEach { byte ->
            crc = crc xor byte
            if (poly != 0L) {
                repeat(8) {
                    crc =
                        if (crc and half != zero) {
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
        crc = crc.toLong().reflected(width).toULong()
    }

    return (crc xor xorOut.toULong() and mask).toString(16).properHex(width)
}

private fun String.properHex(width: Int) =
    if (width > 4) padding("0", ceil(width / 4.0).toInt(), false) else this

fun ByteArray.crcReverse(
    width: Int,
    poly: Int = 0,
    initial: Int = 0x00,
    xorOut: Int = 0x00
): String {
    val mask = (1 shl width) - 1
    var crc = initial
    val diff = 8 - width
    val reversePoly = if (diff > 0) poly.reflected() ushr diff else poly.reflected(width)

    map { it.toInt() }
        .forEach { byte ->
            crc = crc xor byte
            if (poly != 0) {
                repeat(8) {
                    crc =
                        if (crc and 1 == 1) {
                            (crc ushr 1) xor reversePoly
                        } else {
                            crc ushr 1
                        }
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

fun Long.reflected(bits: Int = 8): Long {
    var t = 0L
    repeat(bits) { t = t or ((this ushr it and 0x1) shl (bits - 1 - it)) }
    return t
}
