package me.leon.misc.unit

/**
 * @author Leon
 * @since 2023-07-11 17:45
 * @email deadogone@gmail.com
 */
val UNIT_TYPES = arrayOf("Length", "Weight", "Area", "Pressure", "Temperature")

fun String.unitConvert(type: String): String =
    when (type) {
        "Length" -> lengthUnit()
        "Weight" -> weightUnit()
        "Area" -> areaUnit()
        "Pressure" -> pressureUnit()
        "Temperature" -> temperature()
        else -> error("")
    }
