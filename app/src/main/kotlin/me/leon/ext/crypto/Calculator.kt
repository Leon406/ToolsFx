package me.leon.ext.crypto

import java.math.BigInteger
import java.security.SecureRandom
import me.leon.ctf.rsa.factor
import me.leon.ext.math.*

private const val FACTOR_UPPER = 120_000

enum class Calculator(val algo: String) : ICalculator {
    PLUS("P+Q") {
        override fun calculate(ints: List<BigInteger>): String {
            println(ints)
            return ints[0].add(ints[1]).toString()
        }
    },
    MINUS("P-Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] - (ints[1])).toString()
        }
    },
    MULTIPLY("P*Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] * (ints[1])).toString()
        }
    },
    DIVIDE_REMAINDER("P/Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].divideAndRemainder((ints[1])).joinToString("\n")
        }
    },
    EXPONENT("P^e") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].pow(ints[3].toInt()).toString()
        }
    },
    ROOT("P^(1/e)") {
        override fun calculate(ints: List<BigInteger>): String {
            return with(ints[0].root(ints[3].toInt())) { "${this[0]}\n${this[1]}" }
        }
    },
    MOD("P mod Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] % (ints[1])).toString()
        }
    },
    PLUS_MOD("(P+Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].add(ints[1]).mod(ints[2]).toString()
        }
    },
    MINUS_MOD("(P-Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].subtract(ints[1]).mod(ints[2]).toString()
        }
    },
    MULTIPLY_MOD("(P*Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].multiply(ints[1]).mod(ints[2]).toString()
        }
    },
    DIVIDE_MOD("(P/Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            val isMutualPrime = ints[1].mutualPrime(ints[2])
            val gcd = if (isMutualPrime) BigInteger.ONE else ints[0].gcd(ints[1])
            return (ints[0].divide(gcd).mod(ints[2]) * (ints[1].divide(gcd).modInverse(ints[2])))
                .mod(ints[2])
                .toString()
        }
    },
    MOD_POW("P^e mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].modPow(ints[3], ints[2]).toString()
        }
    },
    INVERSE("P^-1 mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].modInverse(ints[2]).toString()
        }
    },
    PRIME("isPrime(P)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].isProbablePrime(100)).toString()
        }
    },
    FACTOR("factor(N)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[2].factor()).joinToString("\n")
        }
    },
    RSA_D("e^-1 % (P-1)(Q-1)") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[3].invert(ints[0].phi(ints[1])).toString()
        }
    },
    RSA_DECRYPT("C^d mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[5].modPow(ints[4], ints[2]).toString()
        }
    },
    GCD("gcd(P,Q)") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].gcd(ints[1]).toString()
        }
    },
    LCM("lcm(P,Q)") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].lcm(ints[1]).toString()
        }
    },
    KGCD("gcdExt(P,Q)") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].gcdExt(ints[1]).joinToString("\n")
        }
    },
    GEN_PRIME("P bits prime?") {
        override fun calculate(ints: List<BigInteger>): String {
            return BigInteger.probablePrime(ints[0].toInt(), SecureRandom.getInstance("SHA1PRNG"))
                .toString()
        }
    },
    PRIME_NEXT("next prime?") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].nextProbablePrime().toString()
        }
    },
    AND("P & Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].and(ints[1]).toString()
        }
    },
    OR("P | Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].or(ints[1]).toString()
        }
    },
    NOT("~P") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].not().toString()
        }
    },
    AND_NOT("P & ~Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].andNot(ints[1]).toString()
        }
    },
    XOR("P xor Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].xor(ints[1]).toString()
        }
    },
    SHIFT_LEFT("P << e") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].shiftLeft(ints[3].toInt()).toString()
        }
    },
    SHIFT_RIGHT("P >> e") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].shiftRight(ints[3].toInt()).toString()
        }
    },
    COMPLEX0("e*P+d*Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] * ints[3] + ints[1] * ints[4]).toString()
        }
    },
    FACTORIAL("P!") {
        override fun calculate(ints: List<BigInteger>): String {
            val intNum = ints[0].toInt()
            require(intNum in 1..FACTOR_UPPER) { "range: 1<=P<=120000" }
            return intNum.product().toString()
        }
    },
    FACTORIAL_PRIME("P#") {
        override fun calculate(ints: List<BigInteger>): String {
            val intNum = ints[0].toInt()
            require(intNum in 1..FACTOR_UPPER) { "range: 1<=P<=120000" }
            return (1..intNum)
                .map { it.toBigInteger() }
                .filter { it.isProbablePrime(100) }
                .reduce { acc, i -> acc.multiply(i) }
                .toString()
        }
    },
    PERMUTATION("A(P,Q)") {
        override fun calculate(ints: List<BigInteger>): String {
            val p = ints[0].toInt()
            val q = ints[1].toInt()
            require(p in 1..FACTOR_UPPER) { "range: 1<=P<=120000" }
            require(q <= p) { "q must <= p" }
            val proper = if (p == q) BigInteger.ONE else (p - q).product()
            return (p.product() / proper).toString()
        }
    },
    COMBINATION("C(P,Q)") {
        override fun calculate(ints: List<BigInteger>): String {
            val p = ints[0].toInt()
            val q = ints[1].toInt()
            require(p in 1..FACTOR_UPPER) { "range: 1<=P<=120000" }
            require(q <= p) { "q must <= p" }
            val proper = if (p == q) BigInteger.ONE else (p - q).product()
            return (p.product() / (q.product() * proper)).toString()
        }
    },
}

fun Int.product() = (1..this).map { it.toBigInteger() }.reduce { acc, i -> acc.multiply(i) }
