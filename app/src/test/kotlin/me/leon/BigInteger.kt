package me.leon

import java.math.BigDecimal
import java.math.BigInteger
import me.leon.ext.readFromNet

// this = p
fun BigInteger.phi(q: BigInteger) = (this - BigInteger.ONE) * (q - BigInteger.ONE)

// this = p
fun BigInteger.phi(q: String) = phi(BigInteger(q))

// this 关于 other的逆元
fun BigInteger.invert(other: String) = modInverse(other.toBigInteger())

// this = e
fun BigInteger.invert(phi: BigInteger) = modInverse(phi)

fun BigInteger.gcdext(other: BigInteger) = Kgcd.gcdext(this, other)

// this = c
fun BigInteger.decrypt(d: BigInteger, n: BigInteger) = modPow(d, n).toByteArray().decodeToString()

fun BigInteger.n2s() = toByteArray().decodeToString()

fun String.s2n() = BigInteger(toByteArray())

// this = c
fun BigInteger.decrypt(d: String, n: String) = decrypt(BigInteger(d), BigInteger(n))

// this = n
fun BigInteger.factorDb() = getPrimeFromFactorDb(this)

fun List<BigInteger>.phi() =
    filter { it > BigInteger.ZERO }.fold(BigInteger.ONE) { acc, it -> acc * (it - BigInteger.ONE) }

fun List<BigInteger>.propN(n: BigInteger) =
    filter { it < BigInteger.ZERO }.fold(n) { acc, bigInteger -> acc / bigInteger.abs() }

fun getPrimeFromFactorDb(digit: BigInteger) = getPrimeFromFactorDb(digit.toString())

fun getPrimeFromFactorDb(digit: String): List<BigInteger> {
    "http://www.factordb.com/index.php?query=$digit".readFromNet().also {
        "<td>(\\w+)</td>".toRegex().find(it)?.let {
            when (it.groupValues[1]) {
                "P" -> return listOf(digit.toBigInteger())
                "FF" -> "Composite, fully factored"
                "C" ->
                    return listOf(digit.toBigInteger()).also {
                        println("Composite, no factors known")
                    }
                "CF" -> "Composite, factors known"
                else -> return listOf(digit.toBigInteger()).also { println("Unknown") }
            }
        }

        "index\\.php\\?id=\\d+".toRegex().findAll(it).toList().map { it.value }.also {
            if (it.size >= 3) {
                val map = it.filterIndexed { i, _ -> i != 0 }.map { getPrimeFromFactorDbPath(it) }
                //                println("$digit = \n\t ${map.joinToString("\n\t*")}")
                return map
            } else {
                println("无法分解")
                return listOf(digit.toBigInteger())
            }
        }
    }
}

private fun getPrimeFromFactorDbPath(path: String) =
    "http://www.factordb.com/$path".readFromNet().run {
        "value=\"(\\d+)\"".toRegex().find(this)!!.groupValues[1].toBigInteger().also { digit ->
            "<td>(\\w+)</td>".toRegex().find(this)?.let {
                when (it.groupValues[1]) {
                    "P" -> return digit
                    "FF" -> "Composite, fully factored"
                    "C" -> return -digit.also { println("Composite, no factors known") }
                    "CF" -> "Composite, factors known"
                    else -> return digit.also { println("Unknown") }
                }
            }
        }
    }

fun BigInteger.root(n: Int = 2, precision: Int = 2): BigDecimal {
    var x = BigDecimal(divide(n.toBigInteger()))
    var x0 = BigDecimal.ZERO
    var e = BigDecimal("0.1")
    for (i in 1 until precision) e = e.divide(BigDecimal.TEN, i + 1, BigDecimal.ROUND_HALF_EVEN)
    val K = BigDecimal(this)
    val m = BigDecimal(n)
    var i: Long = 0
    while (x.subtract(x0).abs() > e) {
        x0 = x
        x =
            x.add(
                K.subtract(x.pow(n))
                    .divide(m.multiply(x.pow(n - 1)), precision, BigDecimal.ROUND_HALF_EVEN)
            )
        ++i
    }
    println("iterations $i")
    return x
}
