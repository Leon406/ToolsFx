package me.leon.controller

import java.math.BigInteger
import me.leon.ext.catch
import me.leon.ext.crypto.calculatorType
import tornadofx.*

class CalculatorController : Controller() {
    fun calculate(
        alog: String,
        radix: Int,
        params: List<String>,
    ): String =
        catch({ "error $it" }) {
            alog.calculatorType()!!.calculate(
                params.map { if (it.isNotEmpty()) it.toBigInteger(radix) else BigInteger.ZERO }
            )
        }
}
