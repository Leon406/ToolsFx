package me.leon.ext.crypto

import java.math.BigInteger
import kotlin.math.pow
import me.leon.*

enum class Calculator(val algo: String) : ICalculator {
    PLUS("X+Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].add(ints[1]).toString()
        }
    },
    PLUS_MOD("(X+Y) mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].add(ints[1]).mod(ints[2]).toString()
        }
    },
    MINUS("X-Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] - (ints[1])).toString()
        }
    },
    MINUS_MOD("(X-Y) mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].subtract(ints[1]).mod(ints[2]).toString()
        }
    },
    MULTIPY("X*Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] * (ints[1])).toString()
        }
    },
    MULTIPY_MOD("(X*Y) mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].multiply(ints[1]).mod(ints[2]).toString()
        }
    },
    DIVIDE_REMAINDER("X/Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].divideAndRemainder((ints[1])).joinToString("\n")
        }
    },

    DIVIDE_MOD("(X/Y) mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            val isMutualPrime = ints[1].mutualPrime(ints[2])
            val gcd = if (isMutualPrime) BigInteger.ONE else ints[0].gcd(ints[1])
            return (ints[0].divide(gcd).mod(ints[2]) * (ints[1].divide(gcd).modInverse(ints[2]))).mod(ints[2])
                .toString()
        }
    },

    EXPONENT("X^Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].pow(ints[1].toInt()).toString()
        }
    },
    MOD_POW("X^Y mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].modPow(ints[1], ints[2]).toString()
        }
    },
    MOD("X mod Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0] % (ints[1])).toString()
        }
    },
    AND("X & Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].and(ints[1]).toString()
        }
    },
    OR("X | Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].or(ints[1]).toString()
        }
    },
    NOT("~X") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].not().toString()
        }
    },
    AND_NOT("X & ~Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].andNot(ints[1]).toString()
        }
    },
    XOR("X xor Y") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].xor(ints[1]).toString()
        }
    },
    SHIFT_LEFT("X << A") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].shiftLeft(ints[3].toInt()).toString()
        }
    },
    SHIFT_RIGHT("X >> A") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].shiftRight(ints[3].toInt()).toString()
        }
    },
    GCD("gcd(X,Y)") {
        override fun calculate(ints: List<BigInteger>): String {
            return ints[0].gcd(ints[1]).toString()
        }
    },
    LCM("lcm(X,Y)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].lcm(ints[1])).toString()
        }
    },
    KGCD("kgcd(X,Y)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].gcdExt(ints[1])).joinToString("\n")
        }
    },
    INVERSE("X^-1 mod Z") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].modInverse(ints[2])).toString()
        }
    },
    PRIME("Prime(X)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].isProbablePrime(100)).toString()
        }
    },
    FACTOR("factorDb(X)") {
        override fun calculate(ints: List<BigInteger>): String {
            return (ints[0].factorDb()).joinToString("\n")
        }
    },
    ROOT("X^(1/A)") {
        override fun calculate(ints: List<BigInteger>): String {
            val root =
                ints[0].toDouble().pow(1.0 / ints[3].toInt()).toBigDecimal().toBigInteger()
            val remainder =
                if (root == BigInteger.ONE) ints[0] else ints[0].mod(root.pow(ints[3].toInt()))
            return "$root\n$remainder"
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
