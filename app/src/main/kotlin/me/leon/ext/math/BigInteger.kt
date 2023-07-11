package me.leon.ext.math

import java.math.BigInteger
import java.math.BigInteger.ONE
import java.util.*
import me.leon.REG_NON_PRINTABLE
import me.leon.ctf.rsa.THREE
import me.leon.ctf.rsa.TWO
import me.leon.domain.FactorDbResponse
import me.leon.ext.fromJson
import me.leon.ext.readFromNet

// this = p
fun BigInteger.phi(q: BigInteger) = (this - ONE) * (q - ONE)

fun BigInteger.lcm(other: BigInteger) = this * other / gcd(other)

fun BigInteger.mutualPrime(other: BigInteger) = gcd(other) == ONE

// this = e
fun BigInteger.invert(phi: BigInteger): BigInteger = modInverse(phi)

fun BigInteger.gcdExt(other: BigInteger) = Kgcd.gcdext(this, other)

// this = c
fun BigInteger.decrypt2String(d: BigInteger, n: BigInteger): String =
    with(modPow(d, n).n2s()) {
        REG_NON_PRINTABLE.find(this)?.run { modPow(d, n).toString() } ?: this
    }

fun BigInteger.decrypt(d: BigInteger, n: BigInteger): BigInteger = modPow(d, n)

fun BigInteger.n2s() = toByteArray().decodeToString()

fun String.s2n() = BigInteger(toByteArray())

fun ByteArray.toBigInteger() = BigInteger(this)

// this = n
fun BigInteger.factorDb() = getPrimeFromFactorDb(this)

fun List<BigInteger>.phi(): BigInteger =
    filter { it > BigInteger.ZERO }
        .groupBy { it }
        .map { it.key.eulerPhi(it.value.size) }
        .reduce { acc, phi -> acc * phi }

fun List<BigInteger>.phiMutualPrime(e: BigInteger): List<BigInteger> = filter {
    it > BigInteger.ZERO && e.gcd(it.minus(ONE)) == ONE
}

fun List<BigInteger>.product(): BigInteger = reduce { acc, int -> acc * int }

fun List<BigInteger>.propN(n: BigInteger) =
    filter { it < BigInteger.ZERO }.fold(n) { acc, bigInteger -> acc / bigInteger.abs() }

fun BigInteger.eulerPhi(n: Int) = if (n == 1) minus(ONE) else minus(ONE) * pow(n - 1)

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
    require(n > 0) { "n must be > 0" }
    require(signum() >= 0 || (signum() < 0 && n % 2 != 0)) { "$this  n =$n must be even" }

    val sig = this.signum()
    val v = this.abs()
    var high = ONE
    while (high.pow(n) <= v) high = high.shiftLeft(1)
    var low = high.shiftRight(1)
    var mid = ONE
    var midCount = 0
    while (low < high) {
        mid = (low + high).shiftRight(1)
        if (low < mid && mid.pow(n) <= v) {
            low = mid
        } else if (high > mid && mid.pow(n) >= v) high = mid else mid.also { midCount++ }
        if (midCount > 1) break
    }
    return with(mid * sig.toBigInteger()) { arrayOf(this, this@root - this.pow(n)) }
}

/** 连分数 */
fun BigInteger.continuedFraction(another: BigInteger): MutableList<BigInteger> {
    var a = this
    val list = mutableListOf<BigInteger>()
    var b = another
    while (b != BigInteger.ZERO) {
        list.add(a / b)
        a = b.also { b = a % b }
    }
    return list
}

/** 渐进分数线 */
fun List<BigInteger>.convergent(): MutableList<Pair<BigInteger, BigInteger>> {
    var (pbe, paf) = BigInteger.ZERO to ONE
    var (qbe, qaf) = ONE to BigInteger.ZERO
    val convergent = mutableListOf<Pair<BigInteger, BigInteger>>()
    for (int in this) {
        pbe = paf.also { paf = int * paf + pbe }
        qbe = qaf.also { qaf = int * qaf + qbe }
        convergent.add(paf to qaf)
    }
    return convergent
}

/** this is e, common wiener, but it's too slow */
@Suppress("ReturnCount")
fun BigInteger.wiener(n: BigInteger): BigInteger? {
    println("wiener attack-")
    val wienerPQ = wienerPQ(n)
    if (wienerPQ != null) {
        return wienerPQ
    }
    println("wiener attack slow")
    var q0 = ONE
    val m = TWO
    val c1 = m.modPow(this, n)
    val convergent = this.continuedFraction(n).convergent()
    for ((_, q1) in convergent) {
        for (r in 0..20) for (s in 0..20) {
            val d = r.toBigInteger() * q1 + s.toBigInteger() * q0
            val m1 = c1.modPow(d, n)
            if (m1 == m) {
                println("$r $s $d")
                return d
            }
        }
        q0 = q1
    }
    return null
}

/** this is e ,n = p *q */
fun BigInteger.wienerPQ(n: BigInteger): BigInteger? {
    println("wiener attack pq")
    if (this > n.pow(2).multiply(n.root().first())) {
        println("e > n^(1.5) -> this is not guaranteed to work")
    }
    var x = this
    var y = n
    var guessK: BigInteger
    var guessDg: BigInteger
    val guessD: BigInteger
    // val pMinusQDiv2: BigInteger
    // i = 0
    // q[0] = [e/n]
    // n[0] = q[0]
    // d[0] = 1
    val q0 = x / y
    val n0 = q0
    val d0 = ONE
    var temp = x
    x = y
    y = temp.add(y.multiply(q0).negate())

    // i = 1
    // q[1] = [n / (e - n * [e/n])]
    // n[1] = q[0] * q[1] + 1
    // d[1] = q[1]
    var qI = x / y
    val n1 = q0 * qI + ONE
    val d1 = qI
    // i = 2
    var dI2 = d0
    var dI1 = d1
    var dI: BigInteger
    var nI2 = n0
    var nI1 = n1
    var nI: BigInteger
    var i = 1
    val startTime = System.currentTimeMillis()
    while (x.add(-y * qI).signum() != 0 && (System.currentTimeMillis() - startTime) < 500) {
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
        val pPlusQDiv2 = (n - phiN + ONE) / TWO
        val subtract = pPlusQDiv2.pow(2).subtract(n)
        if (subtract < BigInteger.ZERO) break
        val root = subtract.root()
        if (root.last() == BigInteger.ZERO) {
            // ((p-q)/2)^2 = ((p+q)/2)^2 - pq
            // pMinusQDiv2 = root.first()
            // d = (dg / g) = dg / (edg mod k)
            guessD = guessDg / ((this * guessDg) % guessK)
            // (p+q)/2 = (pq - (p-1)*(q-1) + 1)/2
            //            val guessP = pPlusQDiv2 + pMinusQDiv2
            // q = (p+q)/2 - (p-q)/2
            //            val guessQ = pPlusQDiv2 - pMinusQDiv2
            println("Success")
            return guessD
        }
    }
    return null
}

/** 乘法逆元 (n * m) % p == 1. m = n^-1 =n % p */
fun BigInteger.multiplyInverse(modular: BigInteger): BigInteger {
    val (gcd, x, _) = gcdExt(modular)
    require(gcd == ONE) { "has no multiplicative inverse" }
    return x.mod(modular)
}

private val RANDOM = Random()

fun BigInteger.random(from: BigInteger = ONE): BigInteger {
    val bits = bitLength()
    var r = BigInteger(bits, RANDOM)
    while (r < from || r > this) {
        r = BigInteger(bits, RANDOM)
    }
    return r
}

val FOUR = 4.toBigInteger()
val FIVE = 5.toBigInteger()
val SIX = 6.toBigInteger()
val SEVEN = 7.toBigInteger()
val EIGHT = 8.toBigInteger()
val MAP =
    mapOf(
        THREE to TWO,
        FOUR to THREE,
        FIVE to THREE,
        SIX to FIVE,
        SEVEN to FIVE,
    )

/** todo performance */
fun BigInteger.preProbablePrime(): BigInteger {
    if (this < EIGHT) return MAP[this]!!
    var pre: BigInteger
    val nn = (this / SIX) * SIX
    if (this - nn <= ONE) {
        pre = nn - ONE
        if (pre.isProbablePrime(100)) {
            return pre
        }
        pre -= FOUR
    } else {
        pre = nn + ONE
    }
    while (true) {
        if (pre.isProbablePrime(100)) break
        pre -= TWO
        if (pre.isProbablePrime(100)) break
        pre -= FOUR
    }

    return pre
}
