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
data class ColorL(val red: Int, val green: Int, val blue: Int, val alpha: Int = 255) {
    val a: Float
        get() = alpha / 255F

    override fun toString(): String {
        return "#${"".takeIf { alpha == 255 } ?: alpha.hex}${red.hex}${green.hex}${blue.hex}"
    }

    fun toCmyk(): CmykColor {
        val (c, m, y, k) = ColorUtil.rgb2Cmyk(red, green, blue)
        return CmykColor(c, m, y, k)
    }

    fun toHsv(): HsvColor {
        val (hue, saturation, max) = ColorUtil.rgb2Hsv(red, green, blue)
        return HsvColor(hue, saturation, max)
    }
    fun toHsl(): HslColor {
        val (hue, saturation, l) = ColorUtil.rgb2Hsl(red, green, blue)
        return HslColor(hue, saturation, l)
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

    fun toColor() = ColorL(red, green, blue)
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
