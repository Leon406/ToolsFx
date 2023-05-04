package me.leon.math

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * https://cryptobook.nakov.com/asymmetric-key-ciphers/elliptic-curve-cryptography-ecc
 * https://andrea.corbellini.name/
 *
 * @author Leon
 * @since 2022-11-22 17:22
 */
class EccTest {
    // y2 ≡ x3 + 7 (mod 17)
    private val curve = EccCurve(BigInteger.ZERO, 7.toBigInteger(), 17.toBigInteger())

    @Test
    fun pointOnCurve() {
        val points =
            arrayOf(
                EccPoint(BigInteger.ONE, 5.toBigInteger(), curve),
                EccPoint(BigInteger.ONE, 12.toBigInteger(), curve),
                EccPoint(BigInteger.TWO, 7.toBigInteger(), curve),
                EccPoint(BigInteger.TWO, 10.toBigInteger(), curve),
                EccPoint(3.toBigInteger(), BigInteger.ZERO, curve),
                EccPoint(5.toBigInteger(), 8.toBigInteger(), curve),
                EccPoint(5.toBigInteger(), 9.toBigInteger(), curve),
                EccPoint(6.toBigInteger(), 6.toBigInteger(), curve),
            )
        for (point in points) {
            println(point.isOnCurve)
        }
    }

    @Test
    fun ecc() {
        val p = EccPoint(15.toBigInteger(), 13.toBigInteger(), curve)
        val p2 = p + p
        val p3 = p2 + p
        val p4 = p2 + p2
        val p5 = p4 + p
        val p6 = p4 + p2

        assertEquals(INFINITY, INFINITY.scalar(1.toBigInteger()))
        assertEquals(p, p.scalar(1.toBigInteger()))

        val pTimes2 = p.scalar(2.toBigInteger())
        assertEquals(pTimes2, p2)
        assertTrue(pTimes2.x == BigInteger.TWO && pTimes2.y == 10.toBigInteger())
        assertEquals(p.scalar(3.toBigInteger()), p3)

        val pTimes4 = p.scalar(4.toBigInteger())
        println(pTimes4)
        assertEquals(pTimes4, p4)
        assertTrue(pTimes4.x == 12.toBigInteger() && pTimes4.y == BigInteger.ONE)

        assertEquals(p.scalar(5.toBigInteger()), p5)
        println(p2 + p4)
        val pTimes6 = p.scalar(6.toBigInteger())
        println(pTimes6 == p2 + p4)
        assertEquals(pTimes6, p6)
        assertTrue(pTimes6.x == 5.toBigInteger() && pTimes6.y == 8.toBigInteger())
    }

    @Test
    fun keypair() {
        val eccCurve =
            EccCurve(
                "16546484".toBigInteger(),
                "4548674875".toBigInteger(),
                "15424654874903".toBigInteger()
            )
        eccCurve.g = EccPoint("6478678675".toBigInteger(), "5636379357093".toBigInteger(), eccCurve)

        eccCurve.makeKeyPair("546768".toBigInteger())
    }

    @Test
    fun baseG() {
        val curve = EccCurve(BigInteger.ZERO, 7.toBigInteger(), 17.toBigInteger())
        curve.g = EccPoint(5.toBigInteger(), 9.toBigInteger(), curve)
        for (i in 1..18) {
            println(curve.g!!.scalar(i.toBigInteger()))
        }
    }
}

open class EccPoint(
    val x: BigInteger? = null,
    val y: BigInteger? = null,
    var curve: EccCurve? = null
) {
    private val _curve: EccCurve
        get() = requireNotNull(curve)

    val isInfinity: Boolean
        get() = x == null || y == null

    val isOnCurve: Boolean
        get() =
            (requireNotNull(y) * y - requireNotNull(x) * x * x - _curve.a * x - _curve.b) %
                _curve.p == BigInteger.ZERO

    operator fun plus(p2: EccPoint): EccPoint {
        if (isInfinity) return p2
        if (p2.isInfinity) return this
        if (x == p2.x && y != p2.y) return INFINITY

        requireNotNull(x)
        requireNotNull(y)
        requireNotNull(p2.x)
        requireNotNull(p2.y)
        require(isOnCurve)
        require(p2.isOnCurve)

        val k =
            if (x == p2.x && y == p2.y) {
                (3.toBigInteger() * x * x + _curve.a) * (BigInteger.TWO * y).modInverse(_curve.p)
            } else {
                (y - p2.y) * (x - p2.x).modInverse(_curve.p)
            }
        val x3 = k * k - x - p2.x
        return EccPoint(x3 % _curve.p, (k * (x - x3) - y).mod(_curve.p), _curve)
    }

    fun negativePoint() = EccPoint(x, requireNotNull(y).negate() % _curve.p, _curve)

    fun scalar(k: BigInteger): EccPoint =
        if (k < BigInteger.ZERO) {
            negativePoint().scalar(k.negate())
        } else {
            var tmpK: BigInteger = k
            var result: EccPoint = INFINITY
            var addExp: EccPoint = this
            while (tmpK > BigInteger.ZERO) {
                if (tmpK.and(BigInteger.ONE) == BigInteger.ONE) {
                    result = addExp + result
                }
                addExp += addExp
                tmpK = tmpK.shiftRight(1)
            }
            result
        }

    override fun toString(): String {
        return toString(10)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EccPoint) return false
        return x == other.x && y == other.y
    }

    fun toString(radix: Int): String =
        "EccPoint(x=${x?.toString(radix)}, y=${y?.toString(radix)}, curve=${curve?.toString(radix)}"
}

object INFINITY : EccPoint() {
    override fun toString() = "infinity"
}

/** Ep(a,b) */
data class EccCurve(val a: BigInteger, val b: BigInteger, val p: BigInteger) {

    /** base point G ( P = kG) */
    var g: EccPoint? = null

    /** order, depends on g,如果不是素数,可以拆分成子群 计算阶算法 Schoof算法 特例, 如果 p素数, n = p + 1 */
    var n: BigInteger? = null

    /** cofactor, depends on g, 曲线的阶/子群阶 */
    var h = 1

    fun makeKeyPair(private: BigInteger) {
        val public = requireNotNull(g).scalar(private)
        println("private: $private public: $public")
        println("private: $private public: ${public.toString(16)}")
    }

    fun toString(radix: Int): String =
        if (radix == 10) {
            toString()
        } else {
            "EccCurve(a=${a.toString(radix)}, b=${b.toString(radix)}, p=${p.toString(radix)})"
        }
}
