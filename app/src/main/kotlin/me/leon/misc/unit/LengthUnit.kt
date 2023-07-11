package me.leon.misc.unit

/**
 * @author Leon
 * @since 2023-07-03 11:13
 * @email deadogone@gmail.com
 */
val lengthFactor =
    mapOf(
        "m" to 1.0,
        "cm" to 0.01,
        "dm" to 0.1,
        "km" to 1000.0,
        "mm" to 0.001,
        "inch" to 0.0254,
        "in" to 0.0254,
        "mile" to 1609.34,
        "mi" to 1609.34,
        "ft" to 0.3048,
    )

val REG_LENGTH = "([\\d.]+)\\s*([a-zA-Z]+)".toRegex()
val REG_TRIM_ZERO = "\\.?0+(?= )".toRegex()

fun String.lengthUnit(): String =
    REG_LENGTH.matchEntire(this)?.run {
        var len = groupValues[1].toDouble()
        val unit = groupValues[2]
        len *= (lengthFactor[unit] ?: 1.0)
        lengthFormat(len)
    }
        ?: kotlin.run {
            val len = this.trim().toDouble()
            lengthFormat(len)
        }

private fun lengthFormat(len: Double): String {
    return String.format(
            "%.2f m\n%.2f cm\n%.2f inch\n%.2f ft\n%.3f km\n%.3f mile\n",
            len,
            len / lengthFactor["cm"]!!,
            len / lengthFactor["inch"]!!,
            len / lengthFactor["ft"]!!,
            len / lengthFactor["km"]!!,
            len / lengthFactor["mile"]!!,
        )
        .replace(REG_TRIM_ZERO, "")
}
