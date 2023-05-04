package me.leon.misc

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4
import kotlin.math.roundToInt

/**
 * @author Leon
 * @since 2023-04-12 13:47
 * @email deadogone@gmail.com
 */
data class Color(val red: Int, val green: Int, val blue: Int, val alpha: Int = 255) {
    val a: Float
        get() = alpha / 255F

    override fun toString(): String {
        return "#${"".takeIf { alpha == 255 } ?: alpha.hex}${red.hex}${green.hex}${blue.hex}"
    }

    fun toRgbaString(): String {
        return "rgba($red,$green,$blue,$a)"
    }

    fun toCmyk(): CmykColor {
        val (c, m, y, k) = ColorUtil.rgb2Cmyk(red, green, blue)
        return CmykColor(c, m, y, k)
    }

    /** photoshop awt.Color */
    fun toHsv(): HsvColor {
        val (hue, saturation, max) = ColorUtil.rgb2Hsv(red, green, blue)
        return HsvColor(hue, saturation, max)
    }

    /** css */
    fun toHsl(): HslColor {
        val (hue, saturation, l) = ColorUtil.rgb2Hsl(red, green, blue)
        return HslColor(hue, saturation, l)
    }

    companion object {
        /** #FFF #FF0000 #FF00EE00 */
        fun parseColor(color: String): Color {
            require(color.startsWith("#"))
            val c = color.lowercase().substringAfter("#")
            val hexColor =
                when (c.length) {
                    3 -> "ff" + c.chunked(1).joinToString("") { it.repeat(2) }
                    6 -> "ff$c"
                    8 -> c
                    else -> error("Wrong length")
                }

            return parseColor(hexColor.toUInt(16).toInt())
        }

        /** aRGB */
        fun parseColor(color: Int): Color {
            val a = color shr 24 and 0xFF
            val r = color shr 16 and 0xFF
            val g = color shr 8 and 0xFF
            val b = color and 0xFF
            return Color(r, g, b, a)
        }
    }
}

data class CmykColor(val c: Float, val m: Float, val y: Float, val k: Float) {

    val red
        get() = (255 * (1 - c) * (1 - k)).toInt()

    val green
        get() = (255 * (1 - m) * (1 - k)).toInt()

    val blue
        get() = (255 * (1 - y) * (1 - k)).toInt()

    override fun toString(): String {
        return "cmyk(${c.percentRound}%, ${m.percentRound}%, ${y.percentRound}%, ${k.percentRound}%)"
    }

    fun toColor() = Color(red, green, blue)
}

/**
 * NOTE: 需要保留精度 HSV,又称HSB即色相、饱和度、亮度（英语：Hue, Saturation, Brightness） hue [0,360] saturation [0,100]
 * brightness [0,100]
 */
data class HsvColor(val hue: Float, val saturation: Float, val value: Float) {
    override fun toString(): String {
        return "hsv(${hue.roundToInt()},${saturation.percentRound}%,${value.percentRound}%)"
    }
}

data class HslColor(val hue: Float, val saturation: Float, val lightness: Float) {
    override fun toString(): String {
        return "hsl(${hue.roundToInt()},${saturation.percentRound}%,${lightness.percentRound}%)"
    }
}

val Float.percentRound
    get() = (this * 100).roundToInt()

val Int.hex
    get() = toString(16).uppercase().padStart(2, '0')
