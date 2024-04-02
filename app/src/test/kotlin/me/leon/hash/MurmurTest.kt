package me.leon.hash

import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.hash.murmur.Murmur

/**
 * @author Leon
 * @since 2024-04-02 15:05
 * @email deadogone@gmail.com
 */
class MurmurTest {
    @Test
    fun murmur3() {
        val data = "123456".toByteArray()
        assertEquals(3210799800, Murmur.V3.hash(data, data.size, 0))
        assertEquals(4135123539, Murmur.V3.hash(data, data.size, 123))
    }
}
