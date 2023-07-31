package me.leon.hash

import java.io.File
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.TEST_PRJ_DIR
import me.leon.ext.*

/**
 * @author Leon
 * @since 2023-01-17 14:16
 * @email deadogone@gmail.com
 */
class CrcTest {
    private val checkData = "123456789".toByteArray()

    @Test
    fun reflected() {
        assertEquals("a001", 0x8005.reflected(16).toString(16))
        val toByteArray = BigInteger(0x8005.toString()).toByteArray()
        println(
            toByteArray
                .toBinaryString()
                .takeLast(16)
                .also { println(it) }
                .reversed()
                .binary2ByteArray()
                .toHex()
        )
        println(toByteArray.toHex())
        assertEquals(128, 1.reflected())
        assertEquals(64, 2.reflected())
    }

    @Test
    fun crcBelow8() {
        // CRC-4/ITU (CRC-4/G-704)          x4+x+1  Reflect true
        assertEquals("7", checkData.crcReverse(4, 3))
        assertEquals("7", checkData.crc(4, 3, refIn = true, refOut = true))
        // CRC-5/EPC (CRC-5/EPC-C1G2)          x5+x3+1
        assertEquals("00", checkData.crc(5, 9, 9))
        // CRC-5/ITU           x5+x4+x2+1 Reflect true
        assertEquals("07", checkData.crcReverse(5, 0x15))
        assertEquals("07", checkData.crc(5, 0x15, refIn = true, refOut = true))
        // CRC-5/USB           x5+x2+1 Reflect true
        assertEquals("19", checkData.crcReverse(5, 5, 0x1f, 0x1f))
        assertEquals("19", checkData.crc(5, 5, 0x1f, 0x1f, refIn = true, refOut = true))
        // CRC-6/ITU  (CRC-6/G-704)          x6+x+1 Reflect true
        assertEquals("06", checkData.crcReverse(6, 3))
        assertEquals("06", checkData.crc(6, 3, refIn = true, refOut = true))
        // CRC-7/MMC (CRC-7)           x7+x3+1  MultiMediaCard,SD,ect.
        assertEquals("75", checkData.crc(7, 9))
    }

    @Test
    fun crcBelow8Ext() {
        // CRC-3/GSM
        assertEquals("4", checkData.crc(3, 3, xorOut = 7))
        // CRC-3/ROHC
        assertEquals("6", checkData.crc(3, 3, 7, refIn = true, refOut = true))

        // CRC-4/INTERLAKEN
        assertEquals("b", checkData.crc(4, 3, 0xf, 0xf))

        // CRC-6/CDMA2000-A
        assertEquals("0d", checkData.crc(6, 0x27, 0x3f))
        // CRC-6/CDMA2000-B
        assertEquals("3b", checkData.crc(6, 7, 0x3f))
        // CRC-6/DARC
        assertEquals("26", checkData.crc(6, 0x19, refIn = true, refOut = true))
        // CRC-6/GSM
        assertEquals("13", checkData.crc(6, 0x2f, xorOut = 0x3f))

        // CRC-7/ROHC
        assertEquals("53", checkData.crc(7, 0x4f, 0x7f, refIn = true, refOut = true))
        // CRC-7/UMTS
        assertEquals("61", checkData.crc(7, 0x45))
    }

    @Test
    fun crc8() {
        // CRC-8  (CRC-8/SMBUS)             x8+x2+x+1
        assertEquals("f4", checkData.crc(8, 0x7))
        // CRC-8/ITU (CRC-8/I-432-1)          x8+x2+x+1
        assertEquals("a1", checkData.crc(8, 0x7, 0, 0x55))
        // CRC-8/ROHC          x8+x2+x+1 Reflect true
        assertEquals("d0", checkData.crc(8, 0x7, 0xFF, refIn = true, refOut = true))
        assertEquals("d0", checkData.crcReverse(8, 0x7, 0xFF))
        // CRC-8/MAXIM (CRC-8/MAXIM-DOW,DOW-CRC )        x8+x5+x4+1 Reflect true  Maxim(Dallas)'s
        // some devices,e.g. DS18B20
        assertEquals("a1", checkData.crcReverse(8, 0x31))
        assertEquals("a1", checkData.crc(8, 0x31, refIn = true, refOut = true))
    }

    @Test
    fun crc8Ext() {
        // CRC-8/AUTOSAR
        assertEquals("df", checkData.crc(8, 0x2f, 0xff, 0xff))
        // CRC-8/BLUETOOTH
        assertEquals("26", checkData.crcReverse(8, 0xa7))
        // CRC-8/CDMA2000
        assertEquals("da", checkData.crc(8, 0x9b, 0xff))
        // CRC-8/DARC
        assertEquals("15", checkData.crcReverse(8, 0x39))
        // CRC-8/DVB-S2
        assertEquals("bc", checkData.crc(8, 0xd5))
        // CRC-8/GSM-A
        assertEquals("37", checkData.crc(8, 0x1d))
        // CRC-8/GSM-B
        assertEquals("94", checkData.crc(8, 0x49, xorOut = 0xff))
        // CRC-8/HITAG
        assertEquals("b4", checkData.crc(8, 0x1d, 0xff))
        // CRC-8/I-CODE
        assertEquals("7e", checkData.crc(8, 0x1d, 0xfd))
        // CRC-8/LTE
        assertEquals("ea", checkData.crc(8, 0x9b))
        // CRC-8/MIFARE-MAD
        assertEquals("99", checkData.crc(8, 0x1d, 0xc7))
        // CRC-8/NRSC-5
        assertEquals("f7", checkData.crc(8, 0x31, 0xff))
        // CRC-8/OPENSAFETY
        assertEquals("3e", checkData.crc(8, 0x2f))
        // CRC-8/SAE-J1850
        assertEquals("4b", checkData.crc(8, 0x1d, 0xff, 0xff))
        // CRC-8/TECH-3250 ( CRC-8/AES, CRC-8/EBU)
        assertEquals("97", checkData.crcReverse(8, 0x1d, 0xff))
        // CRC-8/WCDMA
        assertEquals("25", checkData.crcReverse(8, 0x9b))
    }

    @Test
    fun crc10() {
        // CRC-10/ATM
        assertEquals("199", checkData.crc(10, 0x233))
        // CRC-10/CDMA2000
        assertEquals("233", checkData.crc(10, 0x3d9, 0x3ff))
        // CRC-10/GSM
        assertEquals("12a", checkData.crc(10, 0x175, xorOut = 0x3ff))
    }

    @Test
    fun crc11() {
        // CRC-11 (CRC-11/FLEXRAY)
        assertEquals("5a3", checkData.crc(11, 0x385, 0x01a))
        // CRC-11/UMTS
        assertEquals("061", checkData.crc(11, 0x307))
    }

    @Test
    fun crc12() {
        // CRC-12/CDMA2000
        assertEquals("d4d", checkData.crc(12, 0xf13, 0xFFF))
        // CRC-12/DECT (X-CRC-12)
        assertEquals("f5b", checkData.crc(12, 0x80f))
        // CRC-12/GSM
        assertEquals("b34", checkData.crc(12, 0xd31, xorOut = 0xFFF))
        // CRC-12/UMTS (CRC-12/3GPP)
        assertEquals("daf", checkData.crc(12, 0x80f, refOut = true))
    }

    @Test
    fun crc13_15() {
        // CRC-13/BBC
        assertEquals("04fa", checkData.crc(13, 0x1cf5))
        // CRC-14/DARC
        assertEquals("082d", checkData.crcReverse(14, 0x0805))
        // CRC-14/GSM
        assertEquals("30ae", checkData.crc(14, 0x202d, xorOut = 0x3FFF))
        // CRC-15/CAN
        assertEquals("059e", checkData.crc(15, 0x4599))
        // CRC-15/MPT1327 ( CRC-15)
        assertEquals("2566", checkData.crc(15, 0x6815, xorOut = 0x0001))
    }

    @Test
    fun crc16() {
        // CRC-16/IBM (CRC-16/ARC,CRC-16, ARC,CRC-16/LHA)          x16+x15+x2+1 Reflect true
        assertEquals("bb3d", checkData.crcReverse(16, 0x8005))
        assertEquals("bb3d", checkData.crc(16, 0x8005, refIn = true, refOut = true))
        assertEquals("bb3d", checkData.crc(16, 0x8005L, refIn = true, refOut = true))
        // CRC-16/MAXIM (CRC-16/MAXIM-DOW)       x16+x15+x2+1 Reflect true
        assertEquals("44c2", checkData.crcReverse(16, 0x8005, xorOut = 0xFFFF))
        assertEquals(
            "44c2",
            checkData.crc(16, 0x8005, xorOut = 0xFFFF, refIn = true, refOut = true)
        )
        assertEquals(
            "44c2",
            checkData.crc(16, 0x8005, xorOut = 0xFFFFL, refIn = true, refOut = true)
        )
        // CRC-16/USB          x16+x15+x2+1 Reflect true
        assertEquals("b4c8", checkData.crcReverse(16, 0x8005, 0xFFFF, 0xFFFF))
        assertEquals("b4c8", checkData.crc(16, 0x8005, 0xFFFF, 0xFFFF, refIn = true, refOut = true))
        assertEquals(
            "b4c8",
            checkData.crc(16, 0x8005L, 0xFFFF, 0xFFFF, refIn = true, refOut = true)
        )
        // CRC-16/MODBUS (MODBUS)     x16+x15+x2+1 Reflect true
        assertEquals("4b37", checkData.crcReverse(16, 0x8005, 0xFFFF))
        assertEquals("4b37", checkData.crc(16, 0x8005, 0xFFFF, refIn = true, refOut = true))
        assertEquals("4b37", checkData.crc(16, 0x8005L, 0xFFFF, refIn = true, refOut = true))
        // CRC-16/CCITT        x16+x12+x5+1 Reflect true
        // CRC-16/KERMIT, CRC-16/BLUETOOTH, CRC-16/CCITT-TRUE, CRC-16/V-41-LSB, CRC-CCITT, KERMIT
        assertEquals("2189", checkData.crcReverse(16, 0x1021))
        assertEquals("2189", checkData.crc(16, 0x1021, refIn = true, refOut = true))
        // CRC-16/CCITT-false       x16+x12+x5+1
        assertEquals("29b1", checkData.crc(16, 0x1021, 0xFFFF))
        // CRC-16/X25 (CRC-16/IBM-SDLC, CRC-16/ISO-HDLC, CRC-16/ISO-IEC-14443-3-B, CRC-B, X-25)
        //    x16+x12+x5+1 Reflect true
        assertEquals("906e", checkData.crcReverse(16, 0x1021, 0xFFFF, 0xFFFF))
        assertEquals("906e", checkData.crc(16, 0x1021, 0xFFFF, 0xFFFF, refIn = true, refOut = true))
        assertEquals(
            "906e",
            checkData.crc(16, 0x1021L, 0xFFFF, 0xFFFF, refIn = true, refOut = true)
        )
        // CRC-16/XMODEM       x16+x12+x5+1
        // CRC-16/ACORN, CRC-16/LTE, CRC-16/V-41-MSB, XMODEM, ZMODEM
        assertEquals("31c3", checkData.crc(16, 0x1021))
        //  CRC-16/DNP          x16+x13+x12+x11+x10+x8+x6+x5+x2+1 Reflect true M-Bus,ect.
        assertEquals("ea82", checkData.crcReverse(16, 0x3D65, 0x0000, 0xFFFF))
        assertEquals("ea82", checkData.crc(16, 0x3D65, 0x0000, 0xFFFF, refIn = true, refOut = true))
        assertEquals(
            "ea82",
            checkData.crc(16, 0x3D65L, 0x0000, 0xFFFF, refIn = true, refOut = true)
        )
    }

    @Test
    fun crc16Ext() {
        // CRC-16/CDMA2000
        assertEquals("4c06", checkData.crc(16, 0xc867, 0xFFFF))
        // CRC-16/CMS
        assertEquals("aee7", checkData.crc(16, 0x8005, 0xFFFF))
        // CRC-16/DDS-110
        assertEquals("9ecf", checkData.crc(16, 0x8005, 0x800d))

        // CRC-16/DECT-R (R-CRC-16)
        assertEquals("007e", checkData.crc(16, 0x0589, xorOut = 0x0001))

        // CRC-16/DECT-X(X-CRC-16)
        assertEquals("007f", checkData.crc(16, 0x0589))

        // CRC-16/EN-13757
        assertEquals("c2b7", checkData.crc(16, 0x3d65, xorOut = 0xFFFF))

        // CRC-16/GENIBUS (CRC-16/DARC, CRC-16/EPC, CRC-16/EPC-C1G2, CRC-16/I-CODE)
        assertEquals("d64e", checkData.crc(16, 0x1021, 0xFFFF, 0xFFFF))

        // CRC-16/GSM
        assertEquals("ce3c", checkData.crc(16, 0x1021, xorOut = 0xFFFF))

        // CRC-16/IBM-3740 (CRC-16/AUTOSAR, CRC-16/CCITT-FALSE)
        assertEquals("29b1", checkData.crc(16, 0x1021, 0xFFFF))

        // CRC-16/ISO-IEC-14443-3-A (CRC-A)
        assertEquals("bf05", checkData.crc(16, 0x1021, 0xc6c6, 0, true, true))
        // CRC-16/LJ1200
        assertEquals("bdf4", checkData.crc(16, 0x6f63))
        // CRC-16/M17
        assertEquals("772b", checkData.crc(16, 0x5935, 0xFFFF))
        // CRC-16/MCRF4XX
        assertEquals("6f91", checkData.crcReverse(16, 0x1021, 0xFFFF))
        // CRC-16/NRSC-5
        assertEquals("a066", checkData.crcReverse(16, 0x080b, 0xFFFF))
        // CRC-16/OPENSAFETY-A
        assertEquals("5d38", checkData.crc(16, 0x5935))
        // CRC-16/OPENSAFETY-B
        assertEquals("20fe", checkData.crc(16, 0x755b))
        // CRC-16/PROFIBUS ( CRC-16/IEC-61158-2)
        assertEquals("a819", checkData.crc(16, 0x1dcf, 0xFFFF, 0xFFFF))
        // CRC-16/RIELLO
        assertEquals("63d0", checkData.crc(16, 0x1021, 0xb2aa, 0, true, true))
        // CRC-16/SPI-FUJITSU ( CRC-16/AUG-CCITT)
        assertEquals("e5cc", checkData.crc(16, 0x1021, 0x1d0f))
        // CRC-16/T10-DIF
        assertEquals("d0db", checkData.crc(16, 0x8bb7))
        // CRC-16/TELEDISK
        assertEquals("0fb3", checkData.crc(16, 0xa097))
        // CRC-16/TMS37157
        assertEquals("26b1", checkData.crc(16, 0x1021, 0x89ec, 0, true, true))
        // CRC-16/UMTS (CRC-16/BUYPASS, CRC-16/VERIFONE)
        assertEquals("fee8", checkData.crc(16, 0x8005))
    }

    @Test
    fun crc17_crc31() {
        // CRC-17/CAN-FD
        assertEquals("04f03", checkData.crc(17, 0x1685b))
        // CRC-21/CAN-FD
        assertEquals("0ed841", checkData.crc(21, 0x102899))
        // CRC-24/BLE
        assertEquals(
            "c25a56",
            checkData.crc(24, 0x00065b, 0x555555, 0, refIn = true, refOut = true)
        )
        // CRC-24/FLEXRAY-A
        assertEquals("7979bd", checkData.crc(24, 0x5d6dcb, 0xfedcba))
        // CRC-24/FLEXRAY-B
        assertEquals("1f23b8", checkData.crc(24, 0x5d6dcb, 0xabcdef))

        // CRC-24/INTERLAKEN
        assertEquals(
            "b4f3e6",
            checkData.crc(24, 0x328b63, 0xFFFFFF, 0xFFFFFF, refIn = false, refOut = false)
        )
        // CRC-24/LTE-A
        assertEquals("cde703", checkData.crc(24, 0x864cfb))
        // CRC-24/LTE-B
        assertEquals("23ef52", checkData.crc(24, 0x800063))

        // CRC-24 (CRC-24/OPENPGP)
        assertEquals("21cf02", checkData.crc(24, 0x864cfb, 0xb704ce))
        // CRC-24/OS-9
        assertEquals("200fa5", checkData.crc(24, 0x800063, 0xFFFFFF, 0xFFFFFF))
        // CRC-30/CDMA
        assertEquals("04c34abf", checkData.crc(30, 0x2030b9c7, 0x3FFFFFFF, 0x3FFFFFFF))
        // CRC-31/PHILIPS
        assertEquals("0ce9e46c", checkData.crc(31, 0x04c11db7, 0x7FFFFFFF, 0x7FFFFFFF))
    }

    @Test
    fun crc32() {

        // CRC-32 (CRC_32/ADCCP) WinRAR  x32+x26+x23+x22+x16+x12+x11+x10+x8+x7+x5+x4+x2+x+1
        // CRC-32/ISO-HDLC, CRC-32/V-42, CRC-32/XZ, PKZIP
        assertEquals(
            "cbf43926",
            checkData.crc(32, 0x04C11DB7L, 0xFFFFFFFFL, 0xFFFFFFFFL, refIn = true, refOut = true)
        )

        // CRC-32/MPEG-2  x32+x26+x23+x22+x16+x12+x11+x10+x8+x7+x5+x4+x2+x+1  32AF78B3
        assertEquals("0376e6e7", checkData.crc(32, 0x04C11DB7L, 0xFFFFFFFFL))
    }

    @Test
    fun crc32Ext() {
        // CRC-32Q (CRC-32/AIXM)
        assertEquals("3010bf7f", checkData.crc(32, 0x814141abL))
        // CRC-32/AUTOSAR
        assertEquals(
            "1697d06a",
            checkData.crc(32, 0xf4acfb13L, 0xFFFFFFFFL, 0xFFFFFFFFL, refIn = true, refOut = true)
        )
        assertEquals("1697d06a", checkData.crcReverse(32, 0xf4acfb13L, 0xFFFFFFFFL, 0xFFFFFFFFL))
        // CRC-32D (CRC-32/BASE91-D)
        assertEquals(
            "87315576",
            checkData.crc(32, 0xa833982bL, 0xFFFFFFFFL, 0xFFFFFFFFL, refIn = true, refOut = true)
        )
        assertEquals("87315576", checkData.crcReverse(32, 0xa833982bL, 0xFFFFFFFFL, 0xFFFFFFFFL))
        // CRC-32/BZIP2 ( CRC-32/AAL5, CRC-32/DECT-B, B-CRC-32)
        assertEquals("fc891918", checkData.crc(32, 0x04c11db7L, 0xFFFFFFFFL, 0xFFFFFFFFL))
        // CRC-32/CD-ROM-EDC
        assertEquals("6ec2edc4", checkData.crc(32, 0x8001801bL, refIn = true, refOut = true))
        // CRC-32/CKSUM (CKSUM, CRC-32/POSIX)
        assertEquals("765e7680", checkData.crc(32, 0x04c11db7L, 0, 0xFFFFFFFFL))
        // CRC-32C (CRC-32/ISCSI,CRC-32/BASE91-C, CRC-32/CASTAGNOLI, CRC-32/INTERLAKEN)
        assertEquals(
            "e3069283",
            checkData.crc(32, 0x1edc6f41L, 0xFFFFFFFFL, 0xFFFFFFFFL, refIn = true, refOut = true)
        )
        // CRC-32/JAMCRC (JAMCRC)
        assertEquals(
            "340bc6d9",
            checkData.crc(32, 0x04c11db7L, 0xFFFFFFFFL, refIn = true, refOut = true)
        )
        assertEquals("340bc6d9", checkData.crcReverse(32, 0x04c11db7L, 0xFFFFFFFFL, 0))
        // CRC-32/MEF
        assertEquals(
            "d2c22f51",
            checkData.crc(32, 0x741b8cd7L, 0xFFFFFFFFL, refIn = true, refOut = true)
        )
        assertEquals("d2c22f51", checkData.crcReverse(32, 0x741b8cd7L, 0xFFFFFFFFL, 0))
        // XFER (CRC-32/XFER)
        assertEquals("bd0be338", checkData.crc(32, 0x000000AFL))
    }

    @Test
    fun crc32Plus() {
        for ((k, v) in CRC_MAPPING) {
            println("$k " + v.crc().digest(checkData))
        }
        // CRC-40/GSM
        assertEquals("d4164fc646", checkData.crc(40, 0x0004820009L, 0, 0xFFFFFFFFFF))
        // CRC-64/ECMA-182
        assertEquals("6c40df5f0b497347", checkData.crc(64, 0x42F0E1EBA9EA3693L))
        // CRC-64/GO-ISO
        assertEquals(
            "b90956c775a41001",
            checkData.crc(
                64,
                0x000000000000001BUL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            )
        )
        // CRC-64/MS
        assertEquals(
            "75d4b74f024eceea",
            checkData.crc(
                64,
                0x259C84CBA6426349UL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            )
        )

        // CRC-64/REDIS
        assertEquals(
            "e9c6d914c4b8d9ca",
            checkData.crc(64, 0xAD93D23594C935A9UL, refIn = true, refOut = true)
        )
        assertEquals("e9c6d914c4b8d9ca", checkData.crcReverse(64, 0xad93d23594c935a9UL))
        // CRC-64/WE
        assertEquals(
            "62ec59e3f1a4f00a",
            checkData.crc(
                64,
                0x42f0e1eba9ea3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = false,
                refOut = false
            )
        )
        // CRC-64/XZ (CRC-64/GO-ECMA)
        assertEquals(
            "995dc9bbdf1939fa",
            checkData.crc(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            )
        )

        assertEquals(
            "995dc9bbdf1939fa",
            checkData.crcReverse(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
            )
        )
    }

    @Test
    fun fileCrc() {
        // CRC-64/XZ (CRC-64/GO-
        File(TEST_PRJ_DIR, "LICENSE")
            .crc(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            )
            .also { println(it) }
        println(File(TEST_PRJ_DIR, "LICENSE").crc(CRC_MAPPING["CRC-64/XZ"]!!))

        CRC(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            )
            .run {
                update(checkData.sliceArray(0..4))
                update(checkData.sliceArray(5..8))
                assertEquals("995dc9bbdf1939fa", digest())
            }
        assertEquals(
            "995dc9bbdf1939fa",
            checkData.crc(
                64,
                0x42F0E1EBA9EA3693UL,
                0xFFFFFFFFFFFFFFFFUL,
                0xFFFFFFFFFFFFFFFFUL,
                refIn = true,
                refOut = true
            )
        )
        val part1 =
            checkData
                .sliceArray(0..4)
                .crc(
                    64,
                    0x42F0E1EBA9EA3693UL,
                    0xFFFFFFFFFFFFFFFFUL,
                    0xFFFFFFFFFFFFFFFFUL,
                    refIn = true,
                    refOut = true
                )
                .toULong(16)
        checkData
            .sliceArray(5..8)
            .crc(64, 0x42F0E1EBA9EA3693UL, part1, 0xFFFFFFFFFFFFFFFFUL, refIn = true, refOut = true)
            .also { println(it) }
    }
}
