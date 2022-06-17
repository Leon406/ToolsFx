package me.leon

import java.math.BigInteger
import me.leon.ext.fromJson
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
fun BigInteger.decrypt(d: BigInteger, n: BigInteger) = modPow(d, n).n2s()

fun BigInteger.n2s() = toByteArray().decodeToString()

fun String.s2n() = BigInteger(toByteArray())

fun ByteArray.toBigInteger() = BigInteger(this)

// this = c
fun BigInteger.decrypt(d: String, n: String) = decrypt(BigInteger(d), BigInteger(n))

// this = n
fun BigInteger.factorDb() = getPrimeFromFactorDb(this)

fun List<BigInteger>.phi(): BigInteger =
    filter { it > BigInteger.ZERO }.fold(BigInteger.ONE) { acc, int ->
        acc * (int - BigInteger.ONE)
    }

fun List<BigInteger>.propN(n: BigInteger) =
    filter { it < BigInteger.ZERO }.fold(n) { acc, bigInteger -> acc / bigInteger.abs() }

fun BigInteger.eulerPhi(n: Int) = minus(BigInteger.ONE) * pow(n - 1)

fun getPrimeFromFactorDb(digit: BigInteger) = getPrimeFromFactorDb(digit.toString())

fun getPrimeFromFactorDb(digit: String): List<BigInteger> {
    return "http://factordb.com/api?query=$digit"
        .readFromNet()
        .fromJson(FactorDbResponse::class.java)
        .factorList
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
