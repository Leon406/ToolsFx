package me.leon.controller

import java.math.BigInteger
import me.leon.DEBUG
import me.leon.ext.*
import me.leon.ext.crypto.calculatorType
import tornadofx.*

class CalculatorController : Controller() {
    fun calculate(
        algo: String,
        radix: Int,
        params: List<String>,
    ): String =
        catch({ "error $it" }) {
            if (DEBUG) println("alg $algo radix $radix")
            algo
                .calculatorType()!!
                .calculate(
                    params.map {
                        if (it.isNotEmpty()) {
                            it.stripAllSpace().toBigInteger(radix)
                        } else {
                            BigInteger.ZERO
                        }
                    }
                )
        }
}
