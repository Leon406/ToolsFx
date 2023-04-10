package me.leon.misc

import kotlin.test.Test
import me.leon.misc.net.dnsSolve

/**
 * @author Leon
 * @since 2023-04-10 8:37
 * @email deadogone@gmail.com
 */
class DnsSolveTest {
    @Test
    fun dns() {
        println(
            dnsSolve(Files.readResourceText("/domains").lines().filterNot { it.startsWith("#") })
        )
    }
}
