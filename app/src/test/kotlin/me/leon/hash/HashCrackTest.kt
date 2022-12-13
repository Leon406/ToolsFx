package me.leon.hash

import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import me.leon.encode.base.BASE58_DICT
import me.leon.encode.base.BASE92_DICT
import me.leon.ext.crypto.*
import me.leon.ext.random
import me.leon.hash

/**
 * @author Leon
 * @since 2022-08-26 9:33
 */
class HashCrackTest {
    private val hash = "c22a563acc2a587afbfaaaa6d67bc6e628872b00bd7e998873881f7c6fdc62fc"

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
        val mask = "?*?*?*?*0727"
        val r = "19970727"
        println(BASE58_DICT.sliceCount(Runtime.getRuntime().availableProcessors()))
        measureTimeMillis { "?d?d?d?d0727".mask { it.hash() == hash }.also { assertEquals(r, it) } }
            .also { println(it) }

        measureTimeMillis { "1997?d?d?d?d".mask { it.hash() == hash }.also { assertEquals(r, it) } }
            .also { println(it) }

        measureTimeMillis {
                mask.maskParallel(dict) { it.hash() == hash }.also { assertEquals(r, it) }
            }
            .also { println(it) }
    }

    @Test
    fun dict22() {
        val hash = "04402679e7f2933b60a30113cbb32e39b31eb437a8350813cfe6abb063dd78de"
        val dict = "0123456789"
        val mask = "flag?*?*?*?*?*"
        val r = "flag34651"

        val cond = { s: String -> s.hash("SHA-256") == hash }

        measureTimeMillis { "flag?d?d?d?d?d".mask(condition = cond).also { assertEquals(r, it) } }
            .also { println(it) }
        measureTimeMillis { mask.mask(dict, cond).also { assertEquals(r, it) } }
            .also { println(it) }
        measureTimeMillis { mask.maskParallel(dict, cond).also { assertEquals(r, it) } }
            .also { println(it) }
    }

    @Test
    fun dict2() {
        val dict = "0123456789"
        val mask = "861709?*?*?*?*?*?*6"
        val cond = { s: String -> s.hash("SHA-256") == hash }
        measureTimeMillis { "861709?d?d?d?d?d?d6".mask(condition = cond).also { println(it) } }
            .also { println(it) }
        measureTimeMillis { mask.mask(dict, cond).also { println(it) } }.also { println(it) }

        measureTimeMillis { mask.maskParallel(dict, cond).also { println(it) } }
            .also { println(it) }
    }

    @Test
    fun passMatch() {
        val dict = "leonshi"
        val pass = "b" + dict.random(3) + "4"
        println(pass)
        val mask = "b?*?*?*?d"
        measureTimeMillis { mask.mask(dict) { it == pass }.also { assertEquals(pass, it) } }
            .also { println(it) }
        measureTimeMillis { mask.maskParallel(dict) { it == pass }.also { assertEquals(pass, it) } }
            .also { println(it) }

        measureTimeMillis { mask.mask(dict) { it == pass }.also { assertEquals(pass, it) } }
            .also { println(it) }
    }

    @Test
    fun parseMask() {
        val customDict = "leon"

        var dicts = "?d?uabc?*?q".parseMask(customDict)
        println(dicts)
        dicts = "abc?d?uabc?*?q".parseMask(customDict)
        println(dicts)
    }

    @Test
    fun randomPassword() {
        repeat(10) { println(BASE92_DICT.random(5)) }
    }
}
