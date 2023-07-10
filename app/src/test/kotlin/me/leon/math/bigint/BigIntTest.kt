package me.leon.math.bigint

import java.math.BigInteger
import kotlin.test.assertEquals
import me.leon.ext.math.*
import org.junit.Test

class BigIntTest {

    @Test
    fun rootN() {
        val int =
            ("2217344750798294937344050117513831761010547351781457575945714176628679412650463329" +
                    "4234669550268044399317656271118568881021332348369140068180238399943422830231427029931" +
                    "826653444453257342990474092233543389488631718467806742449257243340911537016978649186" +
                    "95050507247415283070309")
                .toBigInteger()
        assertEquals(BigInteger.ONE, int.root(30_000).first())
        assertEquals(int, int.root(1).first())
        assertEquals("46482733661604".toBigInteger(), int.root(20).first())
    }

    @Test
    fun crtTest() {
        // 三三数之剩二，五五数之剩三，七七数之剩二。问物几何？
        var data =
            listOf(
                DivideResult("2", "3"),
                DivideResult("3", "5"),
                DivideResult("2", "7"),
            )
        assertEquals(23, crt(data).toInt())

        data =
            listOf(
                DivideResult("43", "87"),
                DivideResult("80", "115"),
                DivideResult("65", "187"),
            )
        assertEquals(1000, crt(data).toInt())

        data =
            listOf(
                DivideResult("13", "100"),
                DivideResult("20", "301"),
            )

        println(crt(data))
    }
}
