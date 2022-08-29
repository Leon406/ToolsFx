package me.leon.hash

import me.leon.hash
import org.junit.Test

/**
 *
 * @author Leon
 * @since 2022-08-26 9:33
 * @email: deadogone@gmail.com
 */
class HashCrackTest {
    @Test
    fun hash() {

        val hash = "c22a563acc2a587afbfaaaa6d67bc6e628872b00bd7e998873881f7c6fdc62fc"
        for (p in 8_617_090_000_000L..8_617_099_999_999L) {
            if (p.toString().hash("SHA-256") == hash) {
                println(p)
                break
            }
        }
    }
}
