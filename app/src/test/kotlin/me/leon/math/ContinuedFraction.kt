package me.leon.math

import kotlin.test.assertContentEquals
import me.leon.ext.math.*
import org.junit.Test

/**
 * @author Leon
 * @since 2022-09-21 16:12
 */
class ContinuedFraction {

    @Test
    fun cf() {
        val fraction = 12_345.toBigInteger().continuedFraction(11_111.toBigInteger())
        assertContentEquals(mutableListOf(1, 9, 246, 1, 4).map { it.toBigInteger() }, fraction)
        assertContentEquals(
            mutableListOf(1 to 1, 10 to 9, 2461 to 2215, 2471 to 2224, 12_345 to 11_111).map {
                it.first.toBigInteger() to it.second.toBigInteger()
            },
            fraction.convergent()
        )
    }

    @Test
    fun mulInverse() {
        val n = 9.toBigInteger()
        val p = 23.toBigInteger()

        println(n.multiplyInverse(p))
    }
}
