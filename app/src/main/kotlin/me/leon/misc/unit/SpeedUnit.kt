package me.leon.misc.unit

/**
 * @author Leon
 * @since 2023-07-03 11:13
 * @email deadogone@gmail.com
 */
val speedFactor =
    mapOf(
        "m/s" to 1.0,
        "ft/s" to 1 / 3.28084,
        "km/h" to 1 / 3.6,
        "mi/h" to 1 / 2.2369363,
        "knot" to 1 / 1.94384,
        "节" to 1 / 1.94384,
    )

val REG_SPEED = "([\\d.]+)\\s*([a-zA-Z节/]+)".toRegex()

fun String.speedUnit(): String =
    REG_SPEED.matchEntire(this)?.run {
        var len = groupValues[1].toDouble()
        val unit = groupValues[2]
        println(groupValues[2])
        len *= (speedFactor[unit] ?: 1.0)
        println(len)

        speedFormat(len)
    }
        ?: kotlin.run {
            val len = this.trim().toDouble()
            speedFormat(len)
        }

private fun speedFormat(len: Double) =
    String.format(
            "%.2f m/s\n%.2f km/h\n%f knot\n%f mi/h\n",
            len,
            len / speedFactor["km/h"]!!,
            len / speedFactor["knot"]!!,
            len / speedFactor["mi/h"]!!
        )
        .replace(REG_TRIM_ZERO, "")
