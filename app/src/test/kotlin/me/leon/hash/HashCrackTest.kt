package me.leon.hash

import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import me.leon.encode.base.*
import me.leon.ext.random
import me.leon.hash
import org.junit.Test

/**
 * @author Leon
 * @since 2022-08-26 9:33
 */
class HashCrackTest {
    val hash = "c22a563acc2a587afbfaaaa6d67bc6e628872b00bd7e998873881f7c6fdc62fc"

    @Test
    fun hash() {
        for (p in 8_617_090_000_000L..8_617_099_999_999L) {
            if (p.toString().hash("SHA-256") == hash) {
                println(p)
                break
            }
        }
    }

    @Test
    fun dict() {
        val hash = "0175501585710a89f5a60dc9ed2f88d7"
        val dict = "0123456789"
        val mask = "1997????"
        println(BASE58_DICT.sliceCount(Runtime.getRuntime().availableProcessors()))
        measureTimeMillis { mask.maskCrack(dict) { it.hash() == hash }.also { println(it) } }
            .also { println(it) }

        measureTimeMillis {
                mask.maskCrackParallel(dict) { it.hash() == hash }.also { println(it) }
            }
            .also { println(it) }
    }

    @Test
    fun dict22() {
        val hash = "04402679e7f2933b60a30113cbb32e39b31eb437a8350813cfe6abb063dd78de"
        val dict = "0123456789"
        val mask = "flag?????"

        val cond = { s: String -> s.hash("SHA-256") == hash }

        measureTimeMillis { mask.maskCrack(dict, cond).also { println(it) } }.also { println(it) }

        measureTimeMillis { mask.maskCrackParallel(dict, cond).also { println(it) } }
            .also { println(it) }
    }

    @Test
    fun dict2() {
        val dict = "0123456789"
        val mask = "861709??????6"
        val cond = { s: String -> s.hash("SHA-256") == hash }
        measureTimeMillis { mask.maskCrack(dict, cond).also { println(it) } }.also { println(it) }

        measureTimeMillis { mask.maskCrackParallel(dict, cond).also { println(it) } }
            .also { println(it) }
    }

    @Test
    fun passMatch() {
        val dict = BASE64_DICT
        val pass = dict.random(3) + "1"
        println(pass)
        val mask = "???1"
        measureTimeMillis { mask.maskCrack(dict) { it == pass }.also { assertEquals(pass, it) } }
            .also { println(it) }
        measureTimeMillis {
                mask.maskCrackParallel(dict) { it == pass }.also { assertEquals(pass, it) }
            }
            .also { println(it) }
    }

    @Test
    fun randomPassword() {
        repeat(10) { println(BASE92_DICT.random(5)) }
    }
}
