package me.leon.hash

import java.io.File
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
data class CRC(
    val width: Int,
    val poly: ULong = 0UL,
    val initial: ULong = 0UL,
    val xorOut: ULong = 0UL,
    val refIn: Boolean = false,
    val refOut: Boolean = false
) {
    private val mask = if (width == 64) 0xFFFFFFFFFFFFFFFFUL else (1UL shl width) - 1UL
    private val half = (1UL shl (width - 1)).coerceAtLeast(0x80UL)
    private val diff = (8 - width)
    private var crc = (if (diff > 0) initial shl diff else initial).toULong()
    private val properPoly = (if (diff > 0) poly shl diff else poly).toULong()

    fun update(bytes: ByteArray) {
        bytes
            .map { it.toULong() }
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
    }

    fun digest(): String {
        if (diff > 0) {
            crc = crc shr diff
        }
        if (refOut) {
            crc = crc.reflected(width)
        }
        return (crc xor xorOut and mask).toString(16).properHex(width)
    }

    fun reset() {
        crc = (if (diff > 0) initial shl diff else initial).toULong()
    }

    fun digest(bytes: ByteArray): String {
        reset()
        update(bytes)
        return digest()
    }
}

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
) =
    CRC(width, poly, initial, xorOut, refIn, refOut).run {
        update(this@crc)
        digest()
    }

fun File.crc(param: CrcParam) = param.run { crc(width, poly, initial, xorOut, refIn, refOut) }

fun File.crc(
    width: Int,
    poly: ULong = 0UL,
    initial: ULong = 0UL,
    xorOut: ULong = 0UL,
    refIn: Boolean = false,
    refOut: Boolean = false
) =
    CRC(width, poly, initial, xorOut, refIn, refOut).run {
        this@crc.inputStream().use {
            val bytes = ByteArray(8092)
            var len: Int
            while (it.read(bytes).also { len = it } != -1) {
                update(bytes.sliceArray(0 until len))
            }
        }
        digest()
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

data class CrcParam(
    val width: Int,
    val poly: ULong = 0UL,
    val initial: ULong = 0UL,
    val xorOut: ULong = 0UL,
    val refIn: Boolean = false,
    val refOut: Boolean = false
)

fun CrcParam.crc() = CRC(width, poly, initial, xorOut, refIn, refOut)

val CRC_MAPPING =
    mapOf(
        "CRC-32" to
            CrcParam(32, 0x04C11DB7UL, 0xFFFFFFFFUL, 0xFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-64" to
            CrcParam(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            ),
        "CRC-8" to CrcParam(8, 0x7U),
        "CRC-16" to CrcParam(16, 0x8005U, refIn = true, refOut = true),
        "CRC-4/ITU" to CrcParam(4, 3U, refIn = true, refOut = true),
        "CRC-5/EPC" to CrcParam(5, 9U, 9U),
        "CRC-5/ITU" to CrcParam(5, 0x15U, refIn = true, refOut = true),
        "CRC-5/USB" to CrcParam(5, 5U, 0x1FU, 0x1FU, refIn = true, refOut = true),
        "CRC-6/ITU" to CrcParam(6, 3U, refIn = true, refOut = true),
        "CRC-7/MMC" to CrcParam(7, 9U),
        "CRC-3/GSM" to CrcParam(3, 3U, xorOut = 7U),
        "CRC-3/ROHC" to CrcParam(3, 3U, 7U, refIn = true, refOut = true),
        "CRC-4/INTERLAKEN" to CrcParam(4, 3U, 0xFU, 0xFU),
        "CRC-6/CDMA2000-A" to CrcParam(6, 0x27U, 0x3FU),
        "CRC-6/CDMA2000-B" to CrcParam(6, 7U, 0x3FU),
        "CRC-6/DARC" to CrcParam(6, 0x19U, refIn = true, refOut = true),
        "CRC-6/GSM" to CrcParam(6, 0x2FU, xorOut = 0x3FU),
        "CRC-7/ROHC" to CrcParam(7, 0x4FU, 0x7FU, refIn = true, refOut = true),
        "CRC-8/ITU" to CrcParam(8, 0x7U, 0U, 0x55U),
        "CRC-8/ROHC" to CrcParam(8, 0x7U, 0xFFU, refIn = true, refOut = true),
        "CRC-8/MAXIM" to CrcParam(8, 0x31U, refIn = true, refOut = true),
        "CRC-8/AUTOSAR" to CrcParam(8, 0x2FU, 0xFFU, 0xFFU),
        "CRC-8/BLUETOOTH" to CrcParam(8, 0xA7U, refIn = true, refOut = true),
        "CRC-8/CDMA2000" to CrcParam(8, 0x9BU, 0xFFU),
        "CRC-8/DARC" to CrcParam(8, 0x39U, refIn = true, refOut = true),
        "CRC-8/DVB-S2" to CrcParam(8, 0xD5U),
        "CRC-8/GSM-A" to CrcParam(8, 0x1DU),
        "CRC-8/GSM-B" to CrcParam(8, 0x49U, xorOut = 0xFFU),
        "CRC-8/HITAG" to CrcParam(8, 0x1DU, 0xFFU),
        "CRC-8/I-CODE" to CrcParam(8, 0x1DU, 0xFDU),
        "CRC-8/LTE" to CrcParam(8, 0x9BU),
        "CRC-8/MIFARE-MAD" to CrcParam(8, 0x1DU, 0xC7U),
        "CRC-8/NRSC-5" to CrcParam(8, 0x31U, 0xFFU),
        "CRC-8/OPENSAFETY" to CrcParam(8, 0x2FU),
        "CRC-8/SAE-J1850" to CrcParam(8, 0x1DU, 0xFFU, 0xFFU),
        "CRC-8/AES" to CrcParam(8, 0x1DU, 0xFFU, refIn = true, refOut = true),
        "CRC-8/WCDMA" to CrcParam(8, 0x9BU, refIn = true, refOut = true),
        "CRC-10/ATM" to CrcParam(10, 0x233U),
        "CRC-10/CDMA2000" to CrcParam(10, 0x3D9U, 0x3FFU),
        "CRC-10/GSM" to CrcParam(10, 0x175U, xorOut = 0x3FFU),
        "CRC-11" to CrcParam(11, 0x385U, 0x01AU),
        "CRC-11/UMTS" to CrcParam(11, 0x307U),
        "CRC-12/CDMA2000" to CrcParam(12, 0xF13U, 0xFFFU),
        "CRC-12/DECT" to CrcParam(12, 0x80FU),
        "CRC-12/GSM" to CrcParam(12, 0xD31U, xorOut = 0xFFFU),
        "CRC-12/UMTS" to CrcParam(12, 0x80FU, refOut = true),
        "CRC-13/BBC" to CrcParam(13, 0x1CF5U),
        "CRC-14/DARC" to CrcParam(14, 0x0805U, refIn = true, refOut = true),
        "CRC-14/GSM" to CrcParam(14, 0x202DU, xorOut = 0x3FFFU),
        "CRC-15/CAN" to CrcParam(15, 0x4599U),
        "CRC-15" to CrcParam(15, 0x6815U, xorOut = 0x0001U),
        "CRC-16/MAXIM" to CrcParam(16, 0x8005U, xorOut = 0xFFFFU, refIn = true, refOut = true),
        "CRC-16/USB" to CrcParam(16, 0x8005U, 0xFFFFU, 0xFFFFU, refIn = true, refOut = true),
        "CRC-16/MODBUS" to CrcParam(16, 0x8005U, 0xFFFFU, refIn = true, refOut = true),
        "CRC-16/CCITT" to CrcParam(16, 0x1021U, refIn = true, refOut = true),
        "CRC-16/CCITT-false" to CrcParam(16, 0x1021U, 0xFFFFU),
        "CRC-16/X25" to CrcParam(16, 0x1021U, 0xFFFFU, 0XFFFFU, refIn = true, refOut = true),
        "CRC-16/XMODEM" to CrcParam(16, 0x1021U),
        "CRC-16/DNP" to CrcParam(16, 0x3D65U, 0x0000U, 0xFFFFU, refIn = true, refOut = true),
        "CRC-16/CDMA2000" to CrcParam(16, 0xC867U, 0xFFFFU),
        "CRC-16/CMS" to CrcParam(16, 0x8005U, 0xFFFFU),
        "CRC-16/DDS-110" to CrcParam(16, 0x8005U, 0x800DU),
        "CRC-16/DECT-R" to CrcParam(16, 0x0589U, xorOut = 0x0001U),
        "CRC-16/DECT-X" to CrcParam(16, 0x0589U),
        "CRC-16/EN-13757" to CrcParam(16, 0x3D65U, xorOut = 0xFFFFU),
        "CRC-16/DARC" to CrcParam(16, 0x1021U, 0xFFFFU, 0xFFFFU),
        "CRC-16/GSM" to CrcParam(16, 0x1021U, xorOut = 0xFFFFU),
        "CRC-16/CCITT-FALSE" to CrcParam(16, 0x1021U, 0xFFFFU),
        "CRC-16/ISO-IEC-14443-3-A" to CrcParam(16, 0x1021U, 0xc6c6U, 0U, true, true),
        "CRC-16/LJ1200" to CrcParam(16, 0x6f63U),
        "CRC-16/M17" to CrcParam(16, 0x5935U, 0xFFFFU),
        "CRC-16/MCRF4XX" to CrcParam(16, 0x1021U, 0xFFFFU, refIn = true, refOut = true),
        "CRC-16/NRSC-5" to CrcParam(16, 0x080bU, 0xFFFFU, refIn = true, refOut = true),
        "CRC-16/OPENSAFETY-A" to CrcParam(16, 0x5935U),
        "CRC-16/OPENSAFETY-B" to CrcParam(16, 0x755bU),
        "CRC-16/PROFIBUS" to CrcParam(16, 0x1dcfU, 0xFFFFU, 0xFFFFU),
        "CRC-16/RIELLO" to CrcParam(16, 0x1021U, 0xB2AAU, 0U, true, true),
        "CRC-16/SPI-FUJITSU" to CrcParam(16, 0x1021U, 0x1d0fU),
        "CRC-16/T10-DIF" to CrcParam(16, 0x8bb7U),
        "CRC-16/TELEDISK" to CrcParam(16, 0xa097U),
        "CRC-16/TMS37157" to CrcParam(16, 0x1021U, 0x89ecU, 0U, true, true),
        "CRC-16/UMTS" to CrcParam(16, 0x8005U),
        "CRC-17/CAN-FD" to CrcParam(17, 0x1685BU),
        "CRC-21/CAN-FD" to CrcParam(21, 0x102899U),
        "CRC-24/BLE" to CrcParam(24, 0x00065BU, 0x555555U, refIn = true, refOut = true),
        "CRC-24/FLEXRAY-A" to CrcParam(24, 0x5d6dcbU, 0xfedcbaU),
        "CRC-24/FLEXRAY-B" to CrcParam(24, 0x5d6dcbU, 0xabcdefU),
        "CRC-24/INTERLAKEN" to CrcParam(24, 0x328b63U, 0xFFFFFFU, 0xFFFFFFU),
        "CRC-24/LTE-A" to CrcParam(24, 0x864cfbU),
        "CRC-24/LTE-B" to CrcParam(24, 0x800063U),
        "CRC-24" to CrcParam(24, 0x864cfbU, 0xb704ceU),
        "CRC-24/OS-9" to CrcParam(24, 0x800063U, 0xFFFFFFU, 0xFFFFFFU),
        "CRC-30/CDMA" to CrcParam(30, 0x2030b9c7U, 0x3FFFFFFFU, 0x3FFFFFFFU),
        "CRC-31/PHILIPS" to CrcParam(31, 0x04c11db7U, 0x7FFFFFFFU, 0x7FFFFFFFU),
        "CRC-32/MPEG-2" to CrcParam(32, 0x04C11DB7UL, 0xFFFFFFFFUL),
        "CRC-32Q" to CrcParam(32, 0x814141abUL),
        "CRC-32/AUTOSAR" to
            CrcParam(32, 0xf4acfb13UL, 0xFFFFFFFFUL, 0xFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-32D" to
            CrcParam(32, 0xa833982bUL, 0xFFFFFFFFUL, 0xFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-32/BZIP2" to CrcParam(32, 0x04c11db7UL, 0xFFFFFFFFUL, 0xFFFFFFFFUL),
        "CRC-32/CD-ROM-EDC" to CrcParam(32, 0x8001801bUL, refIn = true, refOut = true),
        "CRC-32/POSIX" to CrcParam(32, 0x04c11db7UL, 0U, 0xFFFFFFFFUL),
        "CRC-32C" to
            CrcParam(32, 0x1edc6f41UL, 0xFFFFFFFFUL, 0xFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-32/JAMCRC" to CrcParam(32, 0x04c11db7UL, 0xFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-32/MEF" to CrcParam(32, 0x741b8cd7UL, 0xFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-32/XFER" to CrcParam(32, 0x000000AFUL),
        "CRC-40/GSM" to CrcParam(40, 0x0004820009UL, 0U, 0xFFFFFFFFFFU),
        "CRC-64/ECMA-182" to CrcParam(64, 0x42F0E1EBA9EA3693UL),
        "CRC-64/GO-ISO" to
            CrcParam(
                64,
                0x000000000000001BUL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            ),
        "CRC-64/MS" to
            CrcParam(64, 0x259C84CBA6426349UL, 0xFFFFFFFFFFFFFFFFUL, refIn = true, refOut = true),
        "CRC-64/REDIS" to CrcParam(64, 0xAD93D23594C935A9UL, refIn = true, refOut = true),
        "CRC-64/WE" to
            CrcParam(64, 0x42f0e1eba9ea3693UL, 0xFFFFFFFFFFFFFFFFUL, 0xFFFFFFFFFFFFFFFFUL),
        "CRC-64/XZ" to
            CrcParam(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            ),
    )
