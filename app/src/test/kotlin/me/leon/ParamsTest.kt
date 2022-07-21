package me.leon

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ParamsTest(private val p1: Int, private val p2: Int, private val p3: String) {

    @Test
    fun ppp() {
        println("$p1 $p2 $p3")
    }

    // JUnit Matchers assertThat
    @Test
    fun testAssertThatBothContainsString() {
        assertThat("albumen", both(containsString("a")).and(containsString("b")))
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun params() =
            listOf(
                arrayOf(1, 2, "ddd"),
                arrayOf(3, 4, "555"),
            )
    }
}
