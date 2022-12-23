package me.leon.toolsfx.plugin.ext

import java.awt.Color

/**
 * @author Leon
 * @since 2022-12-22 11:39
 * @email deadogone@gmail.com
 */
operator fun Color.minus(other: Color): Color =
    Color(
        (red - other.red).coerceAtLeast(0),
        (green - other.green).coerceAtLeast(0),
        (blue - other.blue).coerceAtLeast(0)
    )

fun Color.invert() = Color(255 - red, 255 - green, 255 - blue)

fun Int.toColor() = Color(this)

val Int.isBlack
    get() = with(Color(this)) { red + blue + green <= 300 }

val Int.isWhite
    get() = with(Color(this)) { red + blue + green > 300 }
