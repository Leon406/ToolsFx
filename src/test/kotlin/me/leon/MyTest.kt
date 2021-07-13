package me.leon

import org.junit.Test
import java.util.zip.CRC32

class MyTest {

    @Test
    fun crc32Test() {
        CRC32().apply {
            update("hello".toByteArray())
        }.value.also { println(it.toString(16)) }
    }
}