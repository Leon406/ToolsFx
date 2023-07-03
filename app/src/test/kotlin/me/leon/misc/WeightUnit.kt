package me.leon.misc

/**
 * @author Leon
 * @since 2023-07-03 11:13
 * @email deadogone@gmail.com
 */
val weightFactor =
    mapOf(
        "mg" to 0.000001,
        "g" to 0.001,
        "kg" to 1.0,
        "oz" to 0.0283495,
        "lb" to 0.453592,
        "t" to 1000.0,
        "mt" to 1000.0,
        "斤" to 0.5,
        "两" to 0.05,
    )

val REG_WEIGHT = "([\\d.]+)\\s*([a-zA-Z斤两]+)".toRegex()

fun String.weightUnit(): String =
    REG_PRESSURE.matchEntire(this)?.run {
        var len = groupValues[1].toDouble()
        val unit = groupValues[2]
        println(groupValues[2])
        len *= (areaFactor[unit] ?: 1.0)

        String.format(
            "%.2f kg\n%.2f lb\n%.2f 斤\n%.2f oz\n%.3f t\n",
            len,
            len / pressureFactor["lb"]!!,
            len / pressureFactor["斤"]!!,
            len / pressureFactor["oz"]!!,
            len / pressureFactor["t"]!!,
        )
    }
        ?: kotlin.run {
            val len = this.trim().toDouble()
            String.format(
                "%.2f kg\n%.2f lb\n%.2f 斤\n%.2f oz\n%.3f t\n",
                len,
                len / pressureFactor["lb"]!!,
                len / pressureFactor["斤"]!!,
                len / pressureFactor["oz"]!!,
                len / pressureFactor["t"]!!,
            )
        }
