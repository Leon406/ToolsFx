package me.leon.ext.crypto

import java.math.BigInteger
import kotlin.math.pow
import me.leon.*

enum class Calculator(val algo: String) : ICalculator {
    PLUS("X+Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints.first().add(ints[1]).toString()
        }
    },
    MINUS("X-Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first() - (ints[1])).toString()
        }
    },
    MULTIPY("X*Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first() * (ints[1])).toString()
        }
    },
    DIVIDE_REMAINDER("X/Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints.first().divideAndRemainder((ints[1])).joinToString("\n")
        }
    },
    MOD("X%Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first() % (ints[1])).toString()
        }
    },
    EXPONENT("X^Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().pow(ints[1].toInt())).toString()
        }
    },
    GCD("gcd(X,Y)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().gcd(ints[1])).toString()
        }
    },
    LCM("lcm(X,Y)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().lcm(ints[1])).toString()
        }
    },
    KGCD("kgcd(X,Y)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().gcdExt(ints[1])).joinToString("\n")
        }
    },
    INVERSE("X^-1 mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().modInverse(ints[2])).toString()
        }
    },
    PRIME("Prime(X)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().isProbablePrime(100)).toString()
        }
    },
    FACTOR("factorDb(X)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().factorDb()).joinToString("\n")
        }
    },
    ROOT("X^(1/A)") {
        override fun calculate(ints: List<BigInteger>): String {
            val root =
                ints.first().toDouble().pow(1.0 / ints[3].toInt()).toBigDecimal().toBigInteger()
            val remainder =
                if (root == BigInteger.ONE) ints[0] else ints[0].mod(root.pow(ints[3].toInt()))
            return "$root\n$remainder"
        }
    },
    MODPOW("X^Y mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints.first().modPow(ints[1], ints[2])).toString()
        }
    },
    COMPLEX0("A*X+B*Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] * ints[3] + ints[1] * ints[4]).toString()
        }
    },
    COMPLEX01("A^X+B^Y+Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].pow(ints[3].toInt()) + ints[1].pow(ints[4].toInt()) + ints[2])
                .toString()
        }
    },

    // ints[2]
    COMPLEX3("X*Y*Z*A*B") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints
                .fold(BigInteger.ONE) { acc, bigInteger -> acc.multiply(bigInteger) }
                .toString()
        }
    },
    COMPLEX4("X^A * Y^B mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].pow(ints[3].toInt()) * ints[1].pow(ints[4].toInt()))
                .mod(ints[2])
                .toString()
        }
    },
    PHI("(X-1)*(Y-1)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].phi(ints[1])).toString()
        }
    },
}
