package me.leon

import java.math.BigInteger
import me.leon.ext.readFromNet

// this = p
fun BigInteger.phi(q: BigInteger) = (this - BigInteger.ONE) * (q - BigInteger.ONE)

// this = p
fun BigInteger.phi(q: String) = phi(BigInteger(q))

fun BigInteger.lcm(other: BigInteger) = this * other / this.gcd(other)

fun BigInteger.mutualPrime(other: BigInteger) = this.gcd(other) == BigInteger.ZERO

// this 关于 other的逆元
fun BigInteger.invert(other: String): BigInteger = modInverse(other.toBigInteger())

// this = e
fun BigInteger.invert(phi: BigInteger): BigInteger = modInverse(phi)

fun BigInteger.gcdExt(other: BigInteger) = Kgcd.gcdext(this, other)

// this = c
fun BigInteger.decrypt(d: BigInteger, n: BigInteger) = modPow(d, n).toByteArray().decodeToString()

fun BigInteger.n2s() = toByteArray().decodeToString()

fun String.s2n() = BigInteger(toByteArray())

fun ByteArray.toBigInteger() = BigInteger(this)

// this = c
fun BigInteger.decrypt(d: String, n: String) = decrypt(BigInteger(d), BigInteger(n))

// this = n
fun BigInteger.factorDb() = getPrimeFromFactorDb(this)

fun List<BigInteger>.phi(): BigInteger =
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
                return it.filterIndexed { i, _ -> i != 0 }.map { getPrimeFromFactorDbPath(it) }
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
                    "FF" -> println("Composite, fully factored")
                    "C" -> return -digit.also { println("Composite, no factors known") }
                    "CF" -> println("Composite, factors known")
                    else -> return digit.also { println("Unknown") }
                }
            }
        }
    }

// ported from
// https://github.com/ryanInf/python2-libnum/blob/316c378ba268577320a239b2af0d766c1c9bfc6d/libnum/common.py
fun BigInteger.root(n: Int = 2): Array<BigInteger> {
    if (this.signum() < 0 && n % 2 == 0) error("n must be even")

    val sig = this.signum()
    val v = this.abs()
    var high = BigInteger.ONE
    while (high.pow(n) <= v) high = high.shiftLeft(1)
    var low = high.shiftRight(1)
    var mid = BigInteger.ONE
    var midCount = 0
    while (low < high) {
        mid = (low + high).shiftRight(1)
        if (low < mid && mid.pow(n) <= v) low = mid
        else if (high > mid && mid.pow(n) >= v) high = mid else mid.also { midCount++ }
        if (midCount > 1) break
    }
    return with(mid * sig.toBigInteger()) { arrayOf(this, this@root - this.pow(n)) }
}
