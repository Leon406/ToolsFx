package me.leon

import kotlin.test.assertEquals
import org.hamcrest.CoreMatchers.both
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mock
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(Parameterized::class)
class ParamsTest(private val p1: Int, private val p2: Int, private val p3: String) {

    @Mock lateinit var mockList: List<Int>

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun ppp() {
        println("$p1 $p2 $p3")
    }

    // JUnit Matchers assertThat
    @Test
    fun testAssertThatBothContainsString() {
        assertThat("albumen", both(containsString("a")).and(containsString("b")))
    }

    @Test
    fun mockTest() {
        //        val list = Mockito.mock(List::class.java)
        `when`(mockList[anyInt()]).thenReturn(100)
        `when`(mockList[1]).thenReturn(666)

        assertEquals(0, mockList.size)
        assertEquals(666, mockList[1])
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
