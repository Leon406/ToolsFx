package me.leon.img

import java.awt.Color
import java.awt.image.*
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import kotlin.math.pow
import kotlin.random.Random

/**
 * @author Leon
 * @since 2022-12-21 11:36
 * @email deadogone@gmail.com
 */
fun BufferedImage.gray() =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height) { y ->
                with(Color(this@gray.getRGB(x, y))) {
                    val gray = (0.3 * red + 0.59 * green + 0.11 * blue).toInt()

                    this@apply.setRGB(x, y, Color(gray, gray, gray).hashCode())
                }
            }
        }
    }

fun BufferedImage.binary(threshold: Int = 172, isGray: Boolean = true) =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height) { y ->
                with(Color(this@binary.getRGB(x, y))) {
                    val gray =
                        if (isGray) {
                            (0.3 * red + 0.59 * green + 0.11 * blue).toInt()
                        } else {
                            (red + green + blue) / 3
                        }
                    val color = 255.takeIf { gray > threshold } ?: 0
                    this@apply.setRGB(x, y, Color(color, color, color).rgb)
                }
            }
        }
    }

fun BufferedImage.cleanBinary() =
    BufferedImage(width, height, type).apply {
        val (threshold, buff) = this@cleanBinary.grayOstu()
        buff.run {
            repeat(width) { x ->
                repeat(height) { y ->
                    with(Color(this.getRGB(x, y))) {
                        val color = 255.takeIf { red > threshold } ?: 0
                        this@apply.setRGB(x, y, Color(color, color, color).rgb)
                    }
                }
            }
        }
        println(threshold)
        // 二值图
        // 去除干扰线条
        removeLines()
    }

private fun BufferedImage.removeLines() {

    for (y in 1 until height - 1) {
        for (x in 1 until width - 1) {
            val center = getRGB(x, y)
            if (center.isBlack) {
                val left = getRGB(x - 1, y)
                val right = getRGB(x + 1, y)
                val top = getRGB(x, y - 1)
                val bottom = getRGB(x, y + 1)
                val leftBottom = getRGB(x - 1, y + 1)
                val leftTop = getRGB(x - 1, y - 1)
                val rightTop = getRGB(x + 1, y - 1)
                val rightBottom = getRGB(x + 1, y + 1)

                if (
                    isLine(left, right, bottom, top, leftBottom, rightTop, leftTop, rightBottom)
                ) {
                    setRGB(x, y, Color.WHITE.rgb)
                }
                //                else {
                //                    val count = intArrayOf(
                //                        left,
                //                        right,
                //                        top,
                //                        bottom,
                //                        leftTop,
                //                        leftBottom,
                //                        rightTop,
                //                        rightBottom
                //                    ).count { it.isWhite }
                //
                //                    println(count)
                //                    if (count > 4) {
                //                        setRGB(x, y, Color.WHITE.rgb)
                //                    }
                //                }
            }
        }
    }
}

private fun isLine(
    left: Int,
    right: Int,
    bottom: Int,
    top: Int,
    leftBottom: Int,
    rightTop: Int,
    leftTop: Int,
    rightBottom: Int
) = left.isWhite &&
        right.isWhite &&
        bottom.isWhite &&
        top.isWhite &&
        leftBottom.isWhite &&
        rightTop.isWhite ||
        leftTop.isWhite &&
        rightBottom.isWhite &&
        leftBottom.isWhite &&
        rightTop.isWhite

fun BufferedImage.sharpen(kernel: Kernel = DEFAULT_KERNEL_3x3) =
    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR).apply {
        ConvolveOp(kernel).filter(this@sharpen, this)
    }

fun BufferedImage.denoise(kernel: Kernel = DENOISE_KERNEL_GAUSSIAN) =
    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR).apply {
        ConvolveOp(kernel).filter(this@denoise, this)
    }

fun BufferedImage.inverse() =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height) { y ->
                with(Color(this@inverse.getRGB(x, y))) { this@apply.setRGB(x, y, invert().rgb) }
            }
        }
    }

fun BufferedImage.grayOstu(): Pair<Int, BufferedImage> {
    val histData = IntArray(256)
    val buff =
        BufferedImage(width, height, type).also {
            repeat(width) { x ->
                repeat(height) { y ->
                    with(Color(this@grayOstu.getRGB(x, y))) {
                        val r = (red * 1.1 + 30).toInt().coerceAtMost(255)
                        val g = (green * 1.1 + 30).toInt().coerceAtMost(255)
                        val b = (blue * 1.1 + 30).toInt().coerceAtMost(255)
                        val gray =
                            (r.toDouble().pow(2.2) * 0.2973 +
                                    (g.toDouble().pow(2.2) * 0.6274) +
                                    b.toDouble().pow(2.2) * 0.0753)
                                .pow(1 / 2.2)
                                .toInt()
                        it.setRGB(x, y, Color(gray, gray, gray).rgb)
                        histData[gray]++
                    }
                }
            }
        }
    // Total number of pixels
    val total = width * height
    var sum = 0f
    for (t in 0..255) sum += (t * histData[t]).toFloat()
    var sumB = 0f
    var wB = 0
    var wF = 0
    var varMax = 0f
    var threshold = 0
    for (t in 0..255) {
        wB += histData[t] // Weight Background
        if (wB == 0) continue
        wF = total - wB // Weight Foreground
        if (wF == 0) break
        sumB += (t * histData[t]).toFloat()
        val mB = sumB / wB // Mean Background
        val mF = (sum - sumB) / wF // Mean Foreground

        // Calculate Between Class Variance
        val varBetween = wB.toFloat() * wF.toFloat() * (mB - mF) * (mB - mF)

        // Check if new maximum found
        if (varBetween > varMax) {
            varMax = varBetween
            threshold = t
        }
    }
    return threshold to buff
}

/** gray image */
fun BufferedImage.ostu(): Int {
    val histData = IntArray(256)

    repeat(width) { x -> repeat(height) { y -> histData[this@ostu.getRGB(x, y) and 0xff]++ } }

    // Total number of pixels
    val total = width * height
    var sum = 0f
    for (t in 0..255) sum += (t * histData[t]).toFloat()
    var sumB = 0f
    var wB = 0
    var wF: Int
    var varMax = 0f
    var threshold = 0
    for (t in 0..255) {
        // Weight Background
        wB += histData[t]
        if (wB == 0) continue
        // Weight Foreground
        wF = total - wB
        if (wF == 0) break
        sumB += (t * histData[t]).toFloat()
        // Mean Background
        val mB = sumB / wB
        // Mean Foreground
        val mF = (sum - sumB) / wF

        // Calculate Between Class Variance
        val varBetween = wB.toFloat() * wF.toFloat() * (mB - mF) * (mB - mF)

        // Check if new maximum found
        if (varBetween > varMax) {
            varMax = varBetween
            threshold = t
        }
    }
    return threshold
}

fun BufferedImage.mirrorHeight() =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height / 2) { y ->
                with(Color(this@mirrorHeight.getRGB(x, y))) {
                    val tmp = Color(this@mirrorHeight.getRGB(x, height - 1 - y)).rgb
                    this@apply.setRGB(x, height - y - 1, rgb)
                    this@apply.setRGB(x, y, tmp)
                }
            }
        }
    }

fun BufferedImage.mirrorWidth() =
    BufferedImage(width, height, type).apply {
        repeat(height) { y ->
            repeat(width / 2) { x ->
                with(Color(this@mirrorWidth.getRGB(x, y))) {
                    val tmp = Color(this@mirrorWidth.getRGB(width - 1 - x, y)).rgb
                    this@apply.setRGB(width - 1 - x, y, rgb)
                    this@apply.setRGB(x, y, tmp)
                }
            }
        }
    }

fun BufferedImage.mosaic(size: Int = 3) =
    BufferedImage(width, height, TYPE_INT_ARGB).apply {
        val g = this.graphics
        for (x in 0 until width - size step size) {
            for (y in 0 until height - size step size) {
                g.color = this@mosaic.getRGB(x, y).toColor()
                g.fillRect(x, y, size, size)
            }
        }

        g.drawImage(this, 0, 0, null)
    }

fun BufferedImage.oilPaint(size: Int = 5) =
    BufferedImage(width, height, TYPE_INT_ARGB).apply {
        val g = this.graphics
        for (x in 0 until width step size) {
            for (y in 0 until height step size) {
                g.color = this@oilPaint.getRGB(x, y).toColor()
                val r = Random.nextInt(20) + size
                g.fillOval(x, y, r, r)
            }
        }
        g.drawImage(this, 0, 0, null)
    }

fun Color.invert() = Color(255 - red, 255 - green, 255 - blue)

fun Int.toColor() = Color(this)

val Int.isBlack
    get() = with(Color(this)) { red + blue + green <= 300 }

val Int.isWhite
    get() = with(Color(this)) { red + blue + green > 300 }
