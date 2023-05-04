package me.leon.misc

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @author Leon
 * @since 2023-04-12 14:08
 * @email deadogone@gmail.com
 */
object ColorUtil {

    fun rgb2Hsv(red: Int, green: Int, blue: Int): Triple<Float, Float, Float> {
        val r: Float = red / 255F
        val g: Float = green / 255F
        val b: Float = blue / 255F
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min
        val hue =
            when (max) {
                min -> 0F
                r -> (60 * (g - b) / delta + (0.takeIf { g >= b } ?: 360))
                g -> (60 * (b - r) / delta + 120)
                b -> (60 * (r - g) / delta + 240)
                else -> 0F
            }

        val saturation = if (max == 0F) 0F else delta / max
        return Triple(hue, saturation, max)
    }

    fun rgb2Hsl(red: Int, green: Int, blue: Int): Triple<Float, Float, Float> {
        val r: Float = red / 255F
        val g: Float = green / 255F
        val b: Float = blue / 255F
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val hue =
            when (max) {
                min -> 0F
                r -> (60 * (g - b) / delta + (0.takeIf { g >= b } ?: 360))
                g -> (60 * (b - r) / delta + 120)
                b -> (60 * (r - g) / delta + 240)
                else -> 0F
            }
        val l = (max + min) / 2
        val saturation = if (l <= 0.5F) delta / (max + min) else delta / (2 - max - min)
        return Triple(hue, saturation, (max + min) / 2)
    }

    fun rgb2Cmyk(red: Int, green: Int, blue: Int): FloatArray {
        val r: Float = red / 255F
        val g: Float = green / 255F
        val b: Float = blue / 255F
        val k = 1 - maxOf(r, g, b)

        return floatArrayOf((1 - r - k) / (1 - k), (1 - g - k) / (1 - k), (1 - b - k) / (1 - k), k)
    }

    fun cmyk2Rgb(c: Float, m: Float, y: Float, k: Float) =
        Triple(
            (255 * (1 - c) * (1 - k)).toInt(),
            (255 * (1 - m) * (1 - k)).toInt(),
            (255 * (1 - y) * (1 - k)).toInt()
        )

    fun hsv2Rgb(h: Float, s: Float, v: Float): Triple<Int, Int, Int> {

        val c = v * s
        val m = v - c
        val x = c * (1 - abs(h / 60F % 2 - 1))
        val (r1, g1, b1) =
            when ((h / 60).toInt()) {
                0 -> Triple(c, x, 0F)
                1 -> Triple(x, c, 0F)
                2 -> Triple(0F, c, x)
                3 -> Triple(0F, x, c)
                4 -> Triple(x, 0F, c)
                5 -> Triple(c, 0F, x)
                else -> error("Unreachable")
            }

        return Triple(
            ((r1 + m) * 255).roundToInt(),
            ((g1 + m) * 255).roundToInt(),
            ((b1 + m) * 255).roundToInt()
        )
    }

    fun hsl2Rgb(h: Float, s: Float, l: Float): Triple<Int, Int, Int> {

        val p =
            if (l < 0.5F) {
                l * (1 + s)
            } else {
                l + s - l * s
            }

        val q = 2 * l - p

        val hk = h / 360F
        val tr = hk + 1 / 3F
        val tg = hk
        val tb = hk - 1 / 3F
        println("11111 $hk    $tr $tg  $tb")
        val rgb =
            mutableListOf(tr, tg, tb)
                .map {
                    if (it < 0) {
                        it + 1F
                    } else if (tg > 1) {
                        it - 1
                    } else {
                        it
                    }
                }
                .also { println(it) }

        fun convert(tmp: Float): Float =
            if (tmp * 6F < 1) {
                q + (p - q) * 6.0F * tmp
            } else if (2F * tmp < 1F) {
                p
            } else if (3F * tmp < 2F) {
                q + (p - q) * ((2.0F / 3.0F) - tmp) * 6.0F
            } else {
                q
            }

        val rr = rgb.map { convert(it) }

        println(rr)

        return Triple(
            (rr[0] * 255).roundToInt(),
            (rr[1] * 255).roundToInt(),
            (rr[2] * 255).roundToInt()
        )
    }
}
