package me.leon.hash

import java.io.File
import java.util.zip.CRC32
import kotlin.test.assertEquals
import me.leon.TEST_PRJ_DIR
import me.leon.ext.crypto.CRC64
import me.leon.ext.crypto.crc32
import me.leon.ext.toHex
import me.leon.hash
import org.junit.Test

class HashTest {
    @Test
    fun crc32() {
        CRC32().apply { update("hello".toByteArray()) }.value.also {
            assertEquals("3610a686", it.toString(16))
        }

        "hello".toByteArray().crc32().also { assertEquals("3610a686", it) }
    }

    @Test
    fun crc64() {
        val readBytes = File(TEST_PRJ_DIR, "LICENSE").readBytes()
        readBytes.run {
            CRC64()
                .apply {
                    update(this@run)
                    assertEquals("ea9848a519ac78d9", this.crcHex())
                }
                .crcDecimal()
                .also { assertEquals("16904341075272693977", it) }
        }
    }

    @Test
    fun hash() {
        "hello".hash().also { assertEquals("5d41402abc4b2a76b9719d911017c592", it) }
        "hello".toByteArray().hash().also {
            assertEquals("5d41402abc4b2a76b9719d911017c592", it.toHex())
        }

        File(TEST_PRJ_DIR, "LICENSE").hash().also {
            assertEquals("219b0e44bbfc8ffd26c6cd91bb3c5138", it)
        }
    }
}
