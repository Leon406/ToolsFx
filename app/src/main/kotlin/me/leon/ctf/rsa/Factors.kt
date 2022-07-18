package me.leon.ctf.rsa

import java.math.BigInteger
import java.math.BigInteger.*
import me.leon.factorDb

/** 时间复杂度 O(sqrt(N)) 优化 加入试除后 素数判断 */
val THREE = 3.toBigInteger()
val MAX_DIVIDER = 10_000.toBigInteger()
const val MAX_FERMAT_ITERATION = 1_000_000
const val MAX_POLLARD_ITERATION = 1_000

/** 小于 1_000_000 时间复杂度 O(sqrt(N)) */
fun BigInteger.trialDivide(maxDivider: BigInteger = MAX_DIVIDER): MutableList<BigInteger> {
    val factors = mutableListOf<BigInteger>()
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return factors.apply { add(this@trialDivide) }
    println("div: start divide")
    // avoid large number slow computation
    if (bitLength() > 100) return factors.apply { add(this@trialDivide.negate()) }
    var n = this
    while (n % TWO == ZERO) {
        factors.add(TWO)
        n /= TWO
    }

    var f = THREE
    while (f.pow(2) <= n) {
        // optimize, avoid prime loop
        if (n.isProbablePrime(100)) break

        if (n % f == ZERO) {
            factors.add(f)
            n /= f
        } else {
            //  f += TWO
            // optimize
            f = f.nextProbablePrime()
        }
        if (f > maxDivider) break
    }
    if (n != ONE) factors.add(if (n.isProbablePrime(100)) n else n.negate())
    println("div: end divide. found ${factors.size} factors")
    return factors
}

/** 适用因子相差较小 时间复杂度 O(|p-q|) */
fun BigInteger.fermat(iteration: Int = 10_000): MutableList<BigInteger> {
    with(sqrtAndRemainder()) {
        if (this.last() != ZERO) {
            var a = first() + ONE
            var count = 0
            var b: BigInteger
            while (count < iteration) {
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
    return mutableListOf(this.negate())
}

fun BigInteger.pollardsRhoFactors(
    iteration: Int = MAX_POLLARD_ITERATION,
    funBias: BigInteger = ONE
): MutableList<BigInteger> {
    val factors = mutableListOf<BigInteger>()
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return factors.apply { add(this@pollardsRhoFactors) }

    var n: BigInteger = this
    var rho: BigInteger

    while (n.pollardsRho(funBias, maxIteration = iteration).also { rho = it } != n) {
        println("rho $rho")
        if (rho < ZERO) return factors.apply { add(rho) }
        factors.add(rho)
        n /= rho
    }
    if (n != ONE) factors.add(n)
    return factors
}

/** 小因子速度更快 时间复杂度 O(n^1/4). */
fun BigInteger.pollardsRho(
    funBias: BigInteger = ONE,
    maxIteration: Int = MAX_POLLARD_ITERATION
): BigInteger {
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return this
    println("rho: start factor $this")
    var iteration = 0L
    var x = TWO
    var y = TWO
    var d = ONE
    // Floyd's cycle-finding algorithm
    // val f = { a: BigInteger -> (a.pow(2) + funBias) % this }

    // Richard Brent's cycle finding method
    val f = { a: BigInteger -> (a.modPow(this - ONE, this) + funBias) % this }
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

fun BigInteger.pollardsPM1Factors(iteration: Int = MAX_POLLARD_ITERATION): MutableList<BigInteger> {
    val factors = mutableListOf<BigInteger>()
    // optimize, avoid prime loop
    if (isProbablePrime(100)) return factors.apply { add(this@pollardsPM1Factors) }

    var n: BigInteger = this
    var rho: BigInteger

    while (n.pMinus1(maxIteration = iteration).also { rho = it } != n) {
        println("rho $rho")
        factors.add(rho)
        if (rho < ZERO) return factors
        n /= rho
    }
    if (n != ONE) factors.add(n)
    return factors
}

fun BigInteger.pMinus1(maxIteration: Int = MAX_POLLARD_ITERATION): BigInteger {
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
            println("pm1: found $gcd iteration: $iteration")
            return gcd
        }
        if (iteration >= maxIteration) return this.negate()
        i += ONE
    }

    println("pm1: not found")
    return this.negate()
}

fun BigInteger.factor(): MutableList<BigInteger> {

    return factor(
        listOf(
            {
                println("---div--- $it")
                it.trialDivide()
            },
            {
                println("---factorDb---")
                it.factorDb()
            },
            {
                println("---fermat---")
                it.fermat(MAX_POLLARD_ITERATION)
            },
            {
                println("---rho: x^2 + 3---")
                it.pollardsRhoFactors(funBias = THREE)
            },
            {
                println("---rho: x^2 + 2---")
                it.pollardsRhoFactors(funBias = TWO)
            },
            {
                println("---rho: x^2 + 1---")
                it.pollardsRhoFactors(funBias = ONE)
            },
            {
                println("---pm1---")
                it.pollardsPM1Factors()
            },
        )
    )
}

typealias FactorFun = (integer: BigInteger) -> List<BigInteger>

private fun BigInteger.factor(funList: List<FactorFun>): MutableList<BigInteger> {
    if (this <= THREE) return mutableListOf(this)
    var last: BigInteger = this
    val factors: MutableList<BigInteger> = mutableListOf()
    for (func in funList) {
        factors.addAll(func(last.abs()))
        last = factors.last()
        if (last >= ZERO) break
        factors.remove(last)
    }

    if (last < ZERO) factors.add(last)
    return factors
}
