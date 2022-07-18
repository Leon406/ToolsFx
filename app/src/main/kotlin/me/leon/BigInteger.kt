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

fun List<BigInteger>.product(): BigInteger = fold(BigInteger.ONE) { acc, int -> acc * int }

fun BigInteger.isMutualPrime(other: BigInteger) = gcd(other) == BigInteger.ONE

fun List<BigInteger>.propN(n: BigInteger) =
    filter { it < BigInteger.ZERO }.fold(n) { acc, bigInteger -> acc / bigInteger.abs() }

fun BigInteger.eulerPhi(n: Int) = minus(BigInteger.ONE) * pow(n - 1)

fun getPrimeFromFactorDb(digit: BigInteger) = getPrimeFromFactorDb(digit.toString())

fun getPrimeFromFactorDb(digit: String): List<BigInteger> {
    return runCatching {
        "http://factordb.com/api?query=$digit"
            .readFromNet(timeout = 3000)
            .fromJson(FactorDbResponse::class.java)
            .factorList
    }
        .getOrElse { mutableListOf(digit.toBigInteger().negate()) }
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

/** this is e */
fun BigInteger.wiener(n: BigInteger): Array<BigInteger> {
    if (this > n.pow(2).multiply(n.root().first())) {
        println("e > n^(1.5) -> this is not guaranteed to work")
    }
    var x = this
    var y = n
    var guessK: BigInteger
    var guessDg: BigInteger
    val guessD: BigInteger
    val pMinusQDiv2: BigInteger
    // i = 0
    // q[0] = [e/n]
    // n[0] = q[0]
    // d[0] = 1
    val q0 = x / y
    val n0 = q0
    val d0 = BigInteger.ONE
    var temp = x
    x = y
    y = temp.add(y.multiply(q0).negate())

    // i = 1
    // q[1] = [n / (e - n * [e/n])]
    // n[1] = q[0] * q[1] + 1
    // d[1] = q[1]
    var qI = x / y
    val n1 = q0 * qI + BigInteger.ONE
    val d1 = qI
    // i = 2
    var dI2 = d0
    var dI1 = d1
    var dI: BigInteger
    var nI2 = n0
    var nI1 = n1
    var nI: BigInteger
    var i = 1
    while (x.add(-y * qI).signum() != 0) {
        // uncomment for debug
        //        println("q[$i] = $qI")
        //        println("n[$i] = $nI")
        //        println("d[$i] = $dI")
        i++
        temp = x
        x = y
        y = temp.add(-y * qI)
        qI = x / y

        // d[i] = q[i] * d[i-1] + d[i-2]
        // n[i] = q[i] * n[i-1] + n[i-2]
        dI = qI * dI1 + dI2
        nI = qI * nI1 + nI2
        dI2 = dI1
        dI1 = dI
        nI2 = nI1
        nI1 = nI

        if (i % 2 == 0) {
            // k / dg = <q[0],...,q[i-1],q[i]+1>
            // k = (q[i] + 1) * n[i-1] + n[i-2]
            guessK = nI1 + nI2
            // dg = (q[i] + 1) * d[i-1] + d[i-2]
            guessDg = dI1 + dI2
        } else {
            // k / dg = <q[0],...,q[i-1],q[i]>
            // k = q[i] * n[i-1] + n[i-2]
            guessK = nI1
            // dg = q[i] * d[i-1] + d[i-2]
            guessDg = dI1
        }

        // phi(n) = (edg) / k
        val phiN = this * guessDg / guessK
        // (p+q)/2 = (pq - (p-1)*(q-1) + 1)/2
        val pPlusQDiv2 = (n - phiN + BigInteger.ONE) / BigInteger.TWO
        val root = pPlusQDiv2.pow(2).subtract(n).root()
        if (root.last() == BigInteger.ZERO) {
            // ((p-q)/2)^2 = ((p+q)/2)^2 - pq
            pMinusQDiv2 = root.first()
            // d = (dg / g) = dg / (edg mod k)
            guessD = guessDg / ((this * guessDg) % guessK)
            // (p+q)/2 = (pq - (p-1)*(q-1) + 1)/2
            val guessP = pPlusQDiv2 + pMinusQDiv2
            // q = (p+q)/2 - (p-q)/2
            val guessQ = pPlusQDiv2 - pMinusQDiv2
            println("Success")
            return arrayOf(guessD, guessP, guessQ)
        }
    }
    return arrayOf()
}
