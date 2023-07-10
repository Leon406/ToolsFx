package me.leon.ext.math

import java.math.BigInteger

object Kgcd {
    /**
     * @param a 第一个参数
     * @param b 第二个参数
     * @param x x的值，前两个组成的数组
     * @param y y的值，前两个组成的数组
     * @return 返回值为 {最大公约数，x的值，y的值}
     */
    fun gcdext(
        a: BigInteger,
        b: BigInteger,
        x: Array<BigInteger>,
        y: Array<BigInteger>
    ): Array<BigInteger> {
        val result = Array<BigInteger>(3) { BigInteger.ONE }
        if (b == BigInteger.ZERO) {
            result[0] = a
            result[1] = x[0]
            result[2] = y[0]
            return result
        }
        val q = a / b
        val tx1 = x[0] - q * x[1]
        val ty1 = y[0] - q * y[1]
        val tx = arrayOf(x[1], tx1)
        val ty = arrayOf(y[1], ty1)
        return gcdext(b, a % b, tx, ty)
    }

    fun gcdext(a: BigInteger, b: BigInteger): Array<BigInteger> {
        val x = arrayOf(BigInteger.ONE, BigInteger.ZERO)
        val y = arrayOf(BigInteger.ZERO, BigInteger.ONE)
        return gcdext(a, b, x, y)
    }
}

fun crt(divideResults: List<DivideResult>): BigInteger {
    val modulusList = divideResults.map { it.quotient }
    val remainders = divideResults.map { it.remainder }
    return crt(remainders, modulusList)
}

fun crt(remainders: List<BigInteger>, modulusList: List<BigInteger>): BigInteger {
    val m = modulusList.reduce { acc, s -> acc * s }
    val lcmOfModulus = modulusList.reduce { acc, bigInteger -> acc.lcm(bigInteger) }
    return modulusList
        .map { m / it }
        .mapIndexed { index, mi -> remainders[index] * mi * mi.modInverse(modulusList[index]) }
        .sumOf { it } % lcmOfModulus
}

data class DivideResult(val remainder: BigInteger, val quotient: BigInteger) {
    constructor(
        remainder: String,
        quotient: String
    ) : this(remainder.toBigInteger(), quotient.toBigInteger())
}
