package me.leon.misc

import java.nio.ByteOrder
import kotlin.test.Test
import me.leon.ext.crypto.HEX_LEAD_REGEX
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex

/**
 * @author Leon
 * @since 2023-08-23 9:40
 * @email deadogone@gmail.com
 */
class EndiaTest {
    @Test
    fun endia() {
        val data =
            arrayOf(
                "12345678",
                "0x12345678",
                "0X12345678",
                "abcd",
            )
        for (datum in data) {
            if (datum.contains(HEX_LEAD_REGEX)) {
                println("$datum\t" + datum.hex2ByteArray().reversedArray().toHex())
            } else {
                println("$datum\t" + datum.toByteArray(Charsets.UTF_16LE).toHex())
            }
        }
        println(ByteOrder.nativeOrder())
        println("abcd".toByteArray().toHex())
        println("abcd".toByteArray().reversedArray().toHex())
        println("abcd".toByteArray(Charsets.UTF_16).toHex())
        println("abcd".toByteArray(Charsets.UTF_16LE).toHex())
        println("abcd".toByteArray(Charsets.UTF_16BE).toHex())
        println("abcd".toByteArray(Charsets.UTF_32).toHex())
        println("abcd".toByteArray(Charsets.UTF_32BE).toHex())
        println("abcd".toByteArray(Charsets.UTF_32LE).toHex())

        println("12345678".hex2ByteArray().toHex())
        println("12345678".hex2ByteArray().reversedArray().toHex())
    }

    @Test
    fun enumEndia() {
        val bit32 = "abcd"
        println(bit32.toHex())
        println("=====> 8bit")
        println(Endia.BIT_REVERSE.convert(bit32))
        println(Endia.BIT_REVERSE.convert("0x0123456789AB"))
        println(Endia.BIT_REVERSE.convert("0x61626364"))
        println("=====> 16bit")
        println(Endia.LE_BE_16BIT.convert(bit32))
        println(Endia.LE_BE_16BIT.convert("0x6100620063006400"))
        println(Endia.BE_16BIT.convert(bit32))
        println(Endia.BE_16BIT.convert("0x0061006200630064"))
        println("=====> 32bit")
        println(Endia.LE_BE_32BIT.convert(bit32))
        println(Endia.LE_BE_32BIT.convert("0x00000061000000620000006300000064"))
        println(Endia.BE_32BIT.convert(bit32))
        println(Endia.BE_32BIT.convert("0x00000061000000620000006300000064"))
    }
}
