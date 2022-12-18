package me.leon

import org.junit.Test

class XorTest {

    @Test
    fun xorCrack() {
        val raw = "dsdfdsf你好"
        val encoded = listOf(7, 66, 86, 85, 7, 66, 84, -41, -34, -111, -41, -106, -34)
        val key = "c123"
        raw.toByteArray()
            .mapIndexed { index, c -> c.toInt() xor key[index % key.length].code }
            .also { println(it) }

        encoded
            .mapIndexed { index, c -> (c xor key[index % key.length].code).toByte() }
            .toByteArray()
            .decodeToString()
            .also { println(it) }
        println(
            raw.toByteArray()
                .mapIndexed { index, c -> (c.toInt() xor encoded[index % encoded.size]).toByte() }
                .toByteArray()
                .decodeToString()
        )
    }
}
