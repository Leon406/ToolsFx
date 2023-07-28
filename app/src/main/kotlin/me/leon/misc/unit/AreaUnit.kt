package me.leon.misc.unit

/**
 * @author Leon
 * @since 2023-07-03 11:13
 * @email deadogone@gmail.com
 */
val areaFactor =
    mapOf(
        "mm2" to 0.000001,
        "cm2" to 0.0001,
        "m2" to 1.0,
        "ha" to 10000.0,
        "km2" to 1000000.0,
        "in2" to 0.00064516,
        "ft2" to 0.092903,
        "ac" to 4046.860338,
        "mi2" to 2589990.616783,
        "mu" to 666.66666667,
        "亩" to 666.66666667,
    )

val REG_AREA = "([\\d.]+)\\s*([a-zA-Z亩2]+)".toRegex()

fun String.areaUnit(): String =
    REG_AREA.matchEntire(this)?.run {
        var len = groupValues[1].toDouble()
        val unit = groupValues[2]
        println(groupValues[2])
        len *= (areaFactor[unit] ?: 1.0)

        areaFormat(len)
    }
        ?: kotlin.run {
            val len = this.trim().toDouble()
            areaFormat(len)
        }

private fun areaFormat(len: Double) =
    String.format(
            "%.2f m²\n%.2f 亩\n%f km²\n%f ha\n%f mi²\n",
            len,
            len / areaFactor["亩"]!!,
            len / areaFactor["km2"]!!,
            len / areaFactor["ha"]!!,
            len / areaFactor["mi2"]!!,
        )
        .replace(REG_TRIM_ZERO, "")
