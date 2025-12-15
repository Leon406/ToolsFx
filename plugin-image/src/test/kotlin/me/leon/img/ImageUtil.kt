package me.leon.img

import java.awt.*
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.pow
import me.leon.config.APP_ROOT
import me.leon.ext.toFile

object ImageUtil {
    fun hammingDistanceSimilarity(imageFile1: File, file2: File): Double {
        val pixels1 = getImgFingerprint(imageFile1)
        val pixels2 = getImgFingerprint(file2)
        // 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
        val hammingDistance = getHammingDistance(pixels1, pixels2)
        // 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
        val similarity = calSimilarity(hammingDistance) * 100
        return similarity
    }

    fun calculateLibs(dir: String) =
        dir.toFile().listFiles()?.filter { it.isFile }?.associateWith { getImgFingerprint(it) }

    fun searchImage(imageFile1: File, libs: Map<File, IntArray>): Pair<File, Double> {
        val pixels1 = getImgFingerprint(imageFile1)
        // 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
        return libs.keys
            .map { it to calSimilarity(getHammingDistance(pixels1, libs[it]!!)) * 100 }
            .maxBy { it.second }
    }

    @Throws(IOException::class)
    private fun getImgFingerprint(imageFile: File): IntArray {
        var image: Image = ImageIO.read(imageFile)
        // 转换至灰度
        image = toGrayscale(image)
        // 缩小成32x32的缩略图
        image = scale(image)
        // 获取灰度像素数组
        var pixels1 = getPixels(image)
        // 获取平均灰度颜色
        val averageColor = getAverageOfPixelArray(pixels1)
        // 获取灰度像素的比较数组（即图像指纹序列）
        pixels1 = getPixelDeviateWeightsArray(pixels1, averageColor)
        return pixels1
    }

    // 将任意Image类型图像转换为BufferedImage类型，方便后续操作
    fun convertToBufferedFrom(srcImage: Image): BufferedImage {
        val bufferedImage =
            BufferedImage(
                srcImage.getWidth(null),
                srcImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB,
            )
        val g = bufferedImage.createGraphics()
        g.drawImage(srcImage, null, null)
        g.dispose()
        return bufferedImage
    }

    // 转换至灰度图
    fun toGrayscale(image: Image): BufferedImage {
        val sourceBuffered = convertToBufferedFrom(image)
        val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
        val op = ColorConvertOp(cs, null)
        val grayBuffered = op.filter(sourceBuffered, null)
        return grayBuffered
    }

    // 缩放至32x32像素缩略图
    fun scale(image: Image): Image {
        var image = image
        image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH)
        return image
    }

    // 获取像素数组
    fun getPixels(image: Image): IntArray {
        val width = image.getWidth(null)
        val height = image.getHeight(null)
        val pixels = convertToBufferedFrom(image).getRGB(0, 0, width, height, null, 0, width)
        return pixels
    }

    // 获取灰度图的平均像素颜色值
    fun getAverageOfPixelArray(pixels: IntArray): Int {
        var color: Color
        var sumRed: Long = 0
        for (i in pixels.indices) {
            color = Color(pixels[i], true)
            sumRed += color.red.toLong()
        }
        val averageRed = (sumRed / pixels.size).toInt()
        return averageRed
    }

    // 获取灰度图的像素比较数组（平均值的离差）
    fun getPixelDeviateWeightsArray(pixels: IntArray, averageColor: Int): IntArray {
        var color: Color
        val dest = IntArray(pixels.size)
        for (i in pixels.indices) {
            color = Color(pixels[i], true)
            dest[i] = if (color.red - averageColor > 0) 1 else 0
        }
        return dest
    }

    // 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
    fun getHammingDistance(a: IntArray, b: IntArray): Int {
        var sum = 0
        for (i in a.indices) {
            sum += if (a[i] == b[i]) 0 else 1
        }
        return sum
    }

    // 通过汉明距离计算相似度
    fun calSimilarity(hammingDistance: Int): Double {
        val length = 32 * 32
        var similarity = (length - hammingDistance) / length.toDouble()

        // 使用指数曲线调整相似度结果
        similarity = similarity.pow(2.0)
        return similarity
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val libs = calculateLibs("$APP_ROOT/app/src/main/resources/img/ctf")!!
        val user = System.getenv("userprofile")

        val file3 = File("$user/Desktop/111.png")
        println(searchImage(file3, libs))
    }
}
