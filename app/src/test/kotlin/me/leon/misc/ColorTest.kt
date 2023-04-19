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
        val color = Color(144, 202, 248)
        println(color)
        val cmykColor = color.toCmyk()
        println(cmykColor)
        println(cmykColor.toColor())

        println(Color.parseColor("#f5f6f7").toRgbaString())
        println(Color.parseColor("#333"))
    }

    @Test
    fun hsv() {
        val r = 144
        val g = 202
        val b = 248
        val color = Color(r, g, b)
        // 207 42 97
        val hsvColor = color.toHsv()
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
        val color = Color(r, g, b)
        // 207 88 77
        println(ColorUtil.rgb2Hsl(r, g, b))
        val (hue, saturation, lightness) = color.toHsl().also { println(it) }

        assertEquals(hue.roundToInt(), 207)
        assertEquals(saturation.percentRound, 88)
        assertEquals(lightness.percentRound, 77)

        val (rc, gc, bc) = ColorUtil.hsl2Rgb(hue, saturation, lightness)
        assertEquals(r, rc)
        assertEquals(b, bc)
        assertEquals(g, gc)
    }
}
