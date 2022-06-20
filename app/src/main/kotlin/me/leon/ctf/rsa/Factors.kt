package me.leon.ctf.rsa

import java.math.BigInteger
import java.math.BigInteger.*

/** 时间复杂度 O(sqrt(N)) 优化 加入试除后 素数判断 */
val THREE = 3.toBigInteger()

fun BigInteger.trialDivide(): MutableList<BigInteger> {
    val factors = mutableListOf<BigInteger>()
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return factors.apply { add(this@trialDivide) }
    println("div: start divide")
    var n = this
    while (n % TWO == BigInteger.ZERO) {
        factors.add(TWO)
        n /= TWO
    }

    var f = THREE
    while (f.pow(2) <= n) {
        // optimize, avoid prime loop
        if (n.isProbablePrime(100)) break

        if (n % f == BigInteger.ZERO) {
            factors.add(f)
            n /= f
        } else {
            f += TWO
        }
    }
    if (n != ONE) factors.add(n)
    println("div: end divide. found ${factors.size} factors")
    return factors
}

fun BigInteger.fermat(): MutableList<BigInteger> {
    with(sqrtAndRemainder()) {
        if (this.last() != BigInteger.ZERO) {
            var a = first() + BigInteger.ONE
            var count = 0
            var b: BigInteger = BigInteger.ONE
            while (count < 10_000) {
                val b1 = a.pow(2) - this@fermat
                b = b1.sqrt()
                count++
                if (b * b == b1) {
                    println("solved iteration $count \n\tp = ${a + b} \n\tq= ${a - b}\n")
                    return mutableListOf(a + b, a - b)
                } else a++
            }
        }
    }
    println("no fermat solution")
    return mutableListOf(this)
}

fun BigInteger.pollardsRhoFactors(iteration: Int = 10_000): MutableList<BigInteger> {
    val factors = mutableListOf<BigInteger>()
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return factors.apply { add(this@pollardsRhoFactors) }

    var n: BigInteger = this
    var rho: BigInteger

    while (n.pollardsRho(maxIteration = iteration).also { rho = it } != n) {
        println("rho $rho")
        factors.add(rho)
        if (rho < ZERO) return factors.apply { add(rho) }
        n /= rho
    }
    if (n != ONE) factors.add(n)
    return factors
}

fun BigInteger.pollardsRho(funBias: BigInteger = ONE, maxIteration: Int = 10_000): BigInteger {
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return this
    println("rho: start factor $this")
    var iteration = 0L
    var x = TWO
    var y = TWO
    var d = ONE
    val f = { a: BigInteger -> (a.pow(2) + funBias) % this }
    while (d == ONE) {
        iteration++
        x = f(x)
        y = f(f(y))
        d = gcd((x - y).abs())
        if (iteration >= maxIteration) return this.negate()
    }
    println("rho: iteration $iteration found $d ")
    return d
}

fun BigInteger.pollardsPM1Factors(iteration: Int = 300): MutableList<BigInteger> {
    val factors = mutableListOf<BigInteger>()
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return factors.apply { add(this@pollardsPM1Factors) }

    var n: BigInteger = this
    var rho: BigInteger

    while (n.pMinus1(maxIteration = iteration).also { rho = it } != n) {
        println("rho $rho")
        factors.add(rho)
        if (rho < ZERO) return factors.apply { add(rho) }
        n /= rho
    }
    if (n != ONE) factors.add(n)
    return factors
}

fun BigInteger.pMinus1(maxIteration: Int = 300): BigInteger {
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return this
    println("pm1: start factor $this")
    var m = TWO
    var i = ONE
    var iteration = 0L
    while (i < this) {
        iteration++
        m = m.pow(i.toInt()) % this
        val gcd = this.gcd(m - ONE)
        if (gcd != ONE) {
            println("pm1: found $gcd")
            return gcd
        }
        if (iteration >= maxIteration) return this.negate()
        i += ONE
    }

    println("pm1: not found")
    return this
}
