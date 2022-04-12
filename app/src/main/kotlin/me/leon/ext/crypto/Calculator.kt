package me.leon.ext.crypto

import java.math.BigInteger
import java.security.SecureRandom
import me.leon.*

enum class Calculator(val algo: String) : ICalculator {
    PLUS("P+Q") {
        override fun calculate(ints: List<BigInteger>): String {
            println(ints)
            return ints[0].add(ints[1]).toString()
        }
    },
    PLUS_MOD("(P+Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].add(ints[1]).mod(ints[2]).toString()
        }
    },
    MINUS("P-Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] - (ints[1])).toString()
        }
    },
    MINUS_MOD("(P-Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].subtract(ints[1]).mod(ints[2]).toString()
        }
    },
    MULTIPY("P*Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] * (ints[1])).toString()
        }
    },
    MULTIPY_MOD("(P*Q) mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].multiply(ints[1]).mod(ints[2]).toString()
        }
    },
    DIVIDE_REMAINDER("P/Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].divideAndRemainder((ints[1])).joinToString("\n")
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
    EXPONENT("P^a") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].pow(ints[3].toInt()).toString()
        }
    },
    MOD_POW("P^a mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].modPow(ints[3], ints[2]).toString()
        }
    },
    MOD("P mod Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] % (ints[1])).toString()
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
    SHIFT_LEFT("P << a") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].shiftLeft(ints[3].toInt()).toString()
        }
    },
    SHIFT_RIGHT("P >> a") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].shiftRight(ints[3].toInt()).toString()
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
    INVERSE("P^-1 mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].modInverse(ints[2]).toString()
        }
    },
    GEN_PRIME("P bits prime?") {
        override fun calculate(ints: List<BigInteger>): String {
            return BigInteger.probablePrime(ints[0].toInt(), SecureRandom.getInstance("SHA1PRNG"))
                .toString()
        }
    },
    PRIME("Prime(P)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].isProbablePrime(100)).toString()
        }
    },
    FACTOR("factorDb(P)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].factorDb()).joinToString("\n")
        }
    },
    ROOT("P^(1/a)") {
        override fun calculate(ints: List<BigInteger>): String {
            return with(ints[0].root(ints[3].toInt())) { "${this[0]}\n${this[1]}" }
        }
    },
    COMPLEX0("a*P+b*Q") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] * ints[3] + ints[1] * ints[4]).toString()
        }
    },
    COMPLEX01("P^a+Q^b+N") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].pow(ints[3].toInt()) + ints[1].pow(ints[4].toInt()) + ints[2])
                .toString()
        }
    },

    // ints[2]
    COMPLEX3("P*Q*N*a*b") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints
                .fold(BigInteger.ONE) { acc, bigInteger -> acc.multiply(bigInteger) }
                .toString()
        }
    },
    COMPLEX4("P^a * Q^b mod N") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].pow(ints[3].toInt()) * ints[1].pow(ints[4].toInt()))
                .mod(ints[2])
                .toString()
        }
    },
    PHI("(P-1)*(Q-1)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].phi(ints[1])).toString()
        }
    },
    PRIME_NEXT("next prime?") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].nextProbablePrime().toString()
        }
    },
    FACTORIAL("P!") {
        override fun calculate(ints: List<BigInteger>): String {
            val intNum = ints[0].toInt()
            if (intNum <= 1 || intNum > 120000) error("range: 1<=P<=120000")
            return (1..intNum)
                .fold(BigInteger.ONE) { acc, i -> acc.multiply(i.toBigInteger()) }
                .toString()
        }
    },
    FACTORIAL_PRIME("P#") {
        override fun calculate(ints: List<BigInteger>): String {
            val intNum = ints[0].toInt()
            if (intNum <= 1 || intNum > 120000) error("range: 1<=P<=120000")
            return (1..intNum)
                .map { it.toBigInteger() }
                .filter { it.isProbablePrime(100) }
                .fold(BigInteger.ONE) { acc, i -> acc.multiply(i) }
                .toString()
        }
    },
}
