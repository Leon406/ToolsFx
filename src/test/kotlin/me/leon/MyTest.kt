package me.leon

import java.util.zip.CRC32
import org.junit.Test

class MyTest {

    @Test
    fun crc32Test() {
        CRC32().apply { update("hello".toByteArray()) }.value.also { println(it.toString(16)) }
    }
}
