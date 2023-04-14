package me.leon.misc

import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Leon
 * @since 2023-04-12 11:18
 * @email deadogone@gmail.com
 */
class ColorTest {
    @Test
    fun color() {
        val colorL = ColorL(144, 202, 248)
        println(colorL)
        val cmykColor = colorL.toCmyk()
        println(cmykColor)
        println(cmykColor.toColor())
    }

    @Test
    fun hsv() {
        val r = 144
        val g = 202
        val b = 248
        val colorL = ColorL(r, g, b)
        // 207 42 97
        val hsvColor = colorL.toHsv()
        println(hsvColor)
        val (rc, gc, bc) = ColorUtil.hsv2Rgb(hsvColor.hue, hsvColor.saturation, hsvColor.value)

        assertEquals(207, hsvColor.hue.roundToInt())
        assertEquals(42, hsvColor.saturation.percentRound)
        assertEquals(97, hsvColor.value.percentRound)

        assertEquals(r, rc)
        assertEquals(b, bc)
        assertEquals(g, gc)
    }

    @Test
    fun hsl() {
        val r = 144
        val g = 202
        val b = 248
        val colorL = ColorL(r, g, b)
        // 207 88 77
        println(ColorUtil.rgb2Hsl(r, g, b))
        val (hue, saturation, lightness) = colorL.toHsl().also { println(it) }

        assertEquals(hue.roundToInt(), 207)
        assertEquals(saturation.percentRound, 88)
        assertEquals(lightness.percentRound, 77)

        val (rc, gc, bc) = ColorUtil.hsl2Rgb(hue, saturation, lightness)
        assertEquals(r, rc)
        assertEquals(b, bc)
        assertEquals(g, gc)
    }
}
