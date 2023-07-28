package me.leon.misc

import kotlin.test.Test
import me.leon.misc.unit.*

/**
 * @author Leon
 * @since 2023-07-03 9:23
 * @email deadogone@gmail.com
 */
class UnitConvertTest {
    @Test
    fun temperature() {
        listOf("100", "100.2", "100C", "100 C", " 100 C", "373.15K", "212F", "671.67R").forEach {
            println(it.temperature())
        }
    }

    @Test
    fun length() {
        listOf(
                "1000",
                "10000dm",
                "1000000 mm",
                "100000 cm",
                "39370.08 in",
                "0.6213712121212122 mi",
                "3280.84 ft",
            )
            .forEach { println(it.lengthUnit()) }
    }

    @Test
    fun weight() {
        listOf(
                "1000 kg",
                "1000000 mg",
                "1000 kg",
                "140 lb",
                "1000 oz",
                "1000 斤",
                "8 两",
                "0.5t",
            )
            .forEach { println(it.weightUnit()) }
    }

    @Test
    fun area() {
        listOf(
                "1000",
                "1000000cm2",
                "1000in2",
                "140ac",
                "1000 亩",
                "8 km2",
            )
            .forEach { println(it.areaUnit()) }
    }

    @Test
    fun pressure() {
        listOf(
                "50000",
                "5 atm",
                "20 kpa",
                "760 torr",
                "14.696 psi",
                "1.01325 bar",
            )
            .forEach { println(it.lowercase().pressureUnit()) }
    }

    @Test
    fun speed() {
        listOf(
                "1m/s",
                "5 knot",
                "5 节",
                "20 m/s",
                "760 mi/h",
                "1 ft/s",
            )
            .forEach { println(it.lowercase().speedUnit()) }
    }
}
