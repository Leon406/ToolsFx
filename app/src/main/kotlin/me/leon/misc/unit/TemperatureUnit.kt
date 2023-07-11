package me.leon.misc.unit

/**
 * @author Leon
 * @since 2023-07-03 11:13
 * @email deadogone@gmail.com
 */
val f2C = { degree: Double -> (degree - 32) / 1.8 }
val k2C = { degree: Double -> (degree - 273.15) }
val r2C = { degree: Double -> degree / 1.8 - 273.15 }

val c2F = { degree: Double -> 32 + degree * 1.8 }
val c2K = { degree: Double -> degree + 273.15 }
val c2R = { degree: Double -> (degree + 273.15) * 1.8 }

fun String.temperature(): String {
    var temp: Double
    return if (endsWith("K")) {
        temp = substringBefore("K").trim().toDouble()
        temp = k2C(temp)
        String.format("%.2f C\n%.2f F\n%.2f R\n", temp, c2F(temp), c2R(temp))
            .replace(REG_TRIM_ZERO, "")
    } else if (endsWith("F")) {
        temp = substringBefore("F").trim().toDouble()
        temp = f2C(temp)
        String.format("%.2f C\n%.2f K\n%.2f R\n", temp, c2K(temp), c2R(temp))
            .replace(REG_TRIM_ZERO, "")
    } else if (endsWith("R")) {
        temp = substringBefore("R").trim().toDouble()
        temp = r2C(temp)
        String.format("%.2f C\n%.2f F\n%.2f K\n", temp, c2F(temp), c2K(temp))
            .replace(REG_TRIM_ZERO, "")
    } else {
        temp = substringBefore("C").trim().toDouble()
        String.format("%.2f F\n%.2f K\n%.2f R\n", c2F(temp), c2K(temp), c2R(temp))
            .replace(REG_TRIM_ZERO, "")
    }
}
