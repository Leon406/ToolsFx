package me.leon.domain

import java.math.BigInteger

data class FactorDbResponse(val id: String, val status: String, val factors: List<List<Any>>) {
    val canFullFactor
        get() = status == "FF"

    val partialFactor
        get() = status == "CF"

    val composeNotFactor
        get() = status == "C"

    val isPrime
        get() = status == "P"

    val allFactorMap
        get() =
            factors.associate {
                it.first().toString().toBigInteger() to (it.last() as Double).toInt()
            }

    val factorList
        get() =
            factors.fold(mutableListOf<BigInteger>()) { acc, factor ->
                acc.also {
                    var bigInt = factor.first().toString().toBigInteger()
                    val ff =
                        when {
                            isPrime || canFullFactor -> BigInteger.ONE
                            composeNotFactor -> BigInteger.ONE.negate()
                            partialFactor ->
                                if (bigInt.isProbablePrime(100)) {
                                    BigInteger.ONE
                                } else {
                                    BigInteger.ONE.negate()
                                }
                            else -> error("what happened")
                        }
                    bigInt *= ff
                    repeat((factor.last() as Double).toInt()) { acc.add(bigInt) }
                }
            }
}
