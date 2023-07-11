package me.leon.misc.unit

/**
 * @author Leon
 * @since 2023-07-03 11:13
 * @email deadogone@gmail.com
 */
val pressureFactor =
    mapOf(
        "mpa" to 1000000.0,
        "hpa" to 100.0,
        "kpa" to 1000.0,
        "bar" to 100000.0,
        "pa" to 1.0,
        "mpa" to 10000.0,
        "torr" to 133.322368,
        "atm" to 101325.0,
        "psi" to 6894.76,
        "ksi" to 6894760.0,
    )

val REG_PRESSURE = "([\\d.]+)\\s*([a-zA-Z]+)".toRegex()

fun String.pressureUnit(): String =
    REG_PRESSURE.matchEntire(this)?.run {
        var len = groupValues[1].toDouble()
        val unit = groupValues[2]
        println(groupValues[2])
        len *= (pressureFactor[unit] ?: 1.0)

        pressureFormat(len)
    }
        ?: kotlin.run {
            val len = this.trim().toDouble()
            pressureFormat(len)
        }

private fun pressureFormat(len: Double) =
    String.format(
            "%.2f Pa\n%.2f kPa\n%.2f torr\n%.2f psi\n%.3f atm\n",
            len,
            len / pressureFactor["kpa"]!!,
            len / pressureFactor["torr"]!!,
            len / pressureFactor["psi"]!!,
            len / pressureFactor["atm"]!!,
        )
        .replace(REG_TRIM_ZERO, "")
