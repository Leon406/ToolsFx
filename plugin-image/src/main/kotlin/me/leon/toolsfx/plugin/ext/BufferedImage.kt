package me.leon.toolsfx.plugin.ext

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.*
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.random.Random
import me.leon.ext.fx.base64Image
import me.leon.ext.fx.toBufferImage
import me.leon.ext.realExtension
import me.leon.ext.toFile

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

fun BufferedImage.binary(threshold: Int = 159, isGray: Boolean = true) =
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

                if (isLine(left, right, bottom, top, leftBottom, rightTop, leftTop, rightBottom)) {
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
) =
    left.isWhite &&
        right.isWhite &&
        bottom.isWhite &&
        top.isWhite &&
        leftBottom.isWhite &&
        rightTop.isWhite ||
        leftTop.isWhite && rightBottom.isWhite && leftBottom.isWhite && rightTop.isWhite

fun BufferedImage.sharpen(kernel: Kernel = DEFAULT_KERNEL_3x3) =
    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR).apply {
        ConvolveOp(kernel).filter(this@sharpen, this)
    }

fun BufferedImage.denoise(kernel: Kernel = DENOISE_KERNEL_GAUSSIAN) =
    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR).apply {
        ConvolveOp(kernel).filter(this@denoise, this)
    }

fun BufferedImage.convolve(kernel: Kernel = EDGE_SOBEL_KERNEL) =
    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR).apply {
        ConvolveOp(kernel).filter(this@convolve, this)
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
    var wF: Int
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

/** 仅适用二值图 求局部最小值(暗的区域变大) */
private fun BufferedImage.erode(kernelSize: Int = 3) =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height) { y ->
                var min = 255
                for (i in x until x + kernelSize) {
                    for (j in y until y + kernelSize) {
                        if (i in 0 until width && j in 0 until height) {
                            min = (this@erode.getRGB(i, j) and 0xFF).coerceAtMost(min)
                        }
                    }
                }
                this@apply.setRGB(x, y, (Color.WHITE.takeIf { min == 255 } ?: Color.BLACK).rgb)
            }
        }
    }

fun BufferedImage.erode(kernelSize: Int = 3, iteration: Int = 1): BufferedImage {
    var bufferedImage = erode(kernelSize)
    repeat(iteration - 1) { bufferedImage = bufferedImage.erode(kernelSize) }
    return bufferedImage
}

/** 仅适用二值图 膨胀 求局部最大值(亮的区域变大) */
private fun BufferedImage.dilate(kernelSize: Int = 3) =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height) { y ->
                var max = 0
                for (i in x until x + kernelSize) {
                    for (j in y until y + kernelSize) {
                        if (i in 0 until width && j in 0 until height) {
                            max = (this@dilate.getRGB(i, j) and 0xFF).coerceAtLeast(max)
                        }
                    }
                }
                this@apply.setRGB(x, y, (Color.WHITE.takeIf { max == 255 } ?: Color.BLACK).rgb)
            }
        }
    }

fun BufferedImage.dilate(kernelSize: Int = 3, iteration: Int = 1): BufferedImage {
    var bufferedImage = dilate(kernelSize)
    repeat(iteration - 1) { bufferedImage = bufferedImage.dilate(kernelSize) }
    return bufferedImage
}

/**
 * 仅适用二值图 先腐蚀后膨胀, 去孤立白点,孔,块
 *
 * 用于消除小物体在纤细点处分离物体、平滑较大物体的边界的同时并不明显改变其面积。
 */
fun BufferedImage.openOp(kernelSize: Int = 3) = erode(kernelSize).dilate(kernelSize)

/**
 * 仅适用二值图 先膨胀后腐蚀, 去孤立黑点,孔,块
 *
 * 用来填充物体内细小空洞、连接邻近物体、平滑其边界的同时并不明显改变其面积。
 */
fun BufferedImage.closeOp(kernelSize: Int = 3) = dilate(kernelSize).erode(kernelSize)

/**
 * 梯度 dilate - erode
 *
 * 保留物体的边缘轮廓。
 */
fun BufferedImage.gradient(kernelSize: Int = 3) = dilate(kernelSize) - erode(kernelSize)

/**
 * 黑帽 close -src 获取轮廓图
 *
 * 突出了比原图轮廓周围的区域更暗的区域 分离比邻近点暗一些的斑块
 */
fun BufferedImage.blackHat(kernelSize: Int = 3) = closeOp(kernelSize) - this

/**
 * 顶帽 src - open 背景提取
 *
 * 突出了比原图轮廓周围的区域更明亮的区域 分离比邻近点亮的斑块, 背景提取
 */
fun BufferedImage.topHat(kernelSize: Int = 3) = this - openOp(kernelSize)

operator fun BufferedImage.minus(other: BufferedImage): BufferedImage =
    BufferedImage(width, height, type).apply {
        repeat(width) { x ->
            repeat(height) { y ->
                val color = this@minus.getRGB(x, y).toColor() - other.getRGB(x, y).toColor()
                setRGB(x, y, color.rgb)
            }
        }
    }

fun BufferedImage.toBinaryString(isBlackOne: Boolean): String = buildString {
    repeat(width) { x ->
        repeat(height) { y ->
            append(1.takeIf { this@toBinaryString.getRGB(x, y).isWhite xor isBlackOne } ?: 0)
        }
    }
}

fun File.toBufferImage(): BufferedImage = ImageIO.read(this)

/**
 * AffineTransformOp.TYPE_BILINEAR 1 AffineTransformOp.TYPE_BICUBIC 2
 * AffineTransformOp.TYPE_NEAREST_NEIGHBOR 3
 */
fun BufferedImage.scale(
    scale: Double,
    interpolationType: Int = AffineTransformOp.TYPE_BILINEAR
): BufferedImage {
    val w2 = (width * scale).toInt()
    val h2 = (height * scale).toInt()
    require(w2 in 1..4096) { "scaled width must in [1,4096]" }
    require(h2 in 1..4096) { "scaled height must in [1,4096]" }
    // BufferedImage#getScaledInstance 性能差,不推荐

    val after = BufferedImage(w2, h2, type)
    val scaleInstance = AffineTransform.getScaleInstance(scale, scale)
    val scaleOp = AffineTransformOp(scaleInstance, interpolationType)
    scaleOp.filter(this, after)
    return after
}

/** 系统 文件长度 路径长度 linux 255 4096 mac 255 1024 win 254 260 win-l 255 32767 */
const val FILE_PATH_MAX_LENGTH = 1024
var FILE_PATH = """[:\\/]""".toRegex()

fun String.autoConvertToBufferImage(): BufferedImage? {
    if (length <= FILE_PATH_MAX_LENGTH && (contains(FILE_PATH))) {
        val file = toFile()
        if (file.exists() && EXTENSION_IMAGE.contains(file.realExtension())) {
            return file.toBufferImage()
        }
    }
    return runCatching { substringAfter(',').base64Image().toBufferImage() }.getOrNull()
}
