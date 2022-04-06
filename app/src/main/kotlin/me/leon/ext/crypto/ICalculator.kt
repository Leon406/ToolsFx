package me.leon.ext.crypto

import java.math.BigInteger

interface ICalculator {
    fun calculate(ints: List<BigInteger>): String
}
