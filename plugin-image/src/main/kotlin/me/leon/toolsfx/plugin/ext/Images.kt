package me.leon.toolsfx.plugin.ext

import java.awt.image.BufferedImage
import java.math.BigInteger
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import kotlin.math.sqrt
import me.leon.ctf.rsa.factor
import me.leon.ext.*
import me.leon.ext.crypto.crc32
import me.leon.ext.fx.*

/**
 * @author Leon
 * @since 2022-12-15 16:17
 * @email deadogone@gmail.com
 */
val EXTENSION_IMAGE = arrayOf("jpg", "png", "gif", "bmp")

/** ihdr width height crc 12-29 ihdr 16-20 width 20-24 height 29-33 crc */
private fun ByteArray.parsePngIhdr() = arrayOf(copyOfRange(12, 29), copyOfRange(29, 33))

private val PNG_MAGIC = byteArrayOf(137.toByte(), 80, 78, 71)

fun ByteArray.fixPng(maxPixel: Int = 2048, minPixel: Int = 5): ByteArray {
    val bytes = this

    for (i in 0..3) {
        if (PNG_MAGIC[i] != bytes[i]) {
            bytes[i] = PNG_MAGIC[i]
        }
    }

    val (ihdr, crc) = bytes.parsePngIhdr()

    if (crc.toHex() == ihdr.crc32()) {
        println("图片未修改")
        return bytes
    }

    val copy = ihdr.copyOf()
    outer@ for (w in minPixel..maxPixel) {
        val tmpWidth = w.pack()
        copy.setByteArray(4, tmpWidth)
        if (crc.toHex() == copy.crc32()) {
            println("width= $w")
            bytes.setByteArray(16, tmpWidth)
            break
        }
        for (h in minPixel..maxPixel) {
            val tmpHeight = h.pack()
            copy.setByteArray(8, tmpHeight)
            if (crc.toHex() == copy.crc32()) {
                println("width= $w height= $h ${tmpHeight.toHex()}")
                bytes.setByteArray(16, tmpWidth)
                bytes.setByteArray(20, tmpHeight)
                break@outer
            }
        }
    }
    return bytes
}

fun String.properByteArray(isFile: Boolean) =
    if (isFile) {
        toFile().readBytes()
    } else {
        toByteArray()
    }

fun String.properString(isFile: Boolean) =
    if (isFile) {
        // 异常处理
        if (length > 2048) {
            this
        } else {
            toFile().readText()
        }
    } else {
        this
    }

val QR_MARK =
    arrayOf(
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(1, 0, 1, 1, 1, 0, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1)
    )

fun List<Int>.fxImage(width: Int, height: Int) =
    BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        .apply {
            createGraphics().apply {
                for (x in 0 until width) for (y in 0 until height) setRGB(
                    x,
                    y,
                    this@fxImage[x * height + y]
                )
            }
        }
        .toFxImg()

fun String.rgbParse(): Int {
    val (r, g, b) = split("\\D".toRegex()).map { it.toInt() }
    return Color.rgb(r, g, b).hashCode()
}

fun Int.toColorInt(isBlackOne: Boolean = true) =
    when {
        isBlackOne && this == 0 -> 0xFFFFFF
        !isBlackOne && this == 1 -> 0xFFFFFF
        else -> 0
    }

fun String.zeroOneImage(isNormal: Boolean = true, isBlackOne: Boolean = true) =
    with(sqrt(stripAllSpace().length.toDouble())) {
        val size = this.toInt()
        val ratio = (360 / size).coerceAtLeast(1)
        val targetSize = size * ratio
        BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB)
            .apply {
                createGraphics().apply {
                    val bytes = filter { it in charArrayOf('0', '1') }
                    for (x in 0 until size) for (y in 0 until size) {
                        for (xBias in 0 until ratio) for (yBias in 0 until ratio) {
                            setRGB(
                                x * ratio + xBias,
                                y * ratio + yBias,
                                when {
                                    isNormal ->
                                        bytes[y * size + x]
                                            .toString()
                                            .toInt()
                                            .toColorInt(isBlackOne)
                                    x < 7 && y < 7 -> QR_MARK[x][y].toColorInt()
                                    x > size - 8 && y < 7 ->
                                        QR_MARK[x - (size - 7)][y].toColorInt(isBlackOne)
                                    x < 7 && y > size - 8 ->
                                        QR_MARK[x][y - (size - 7)].toColorInt(isBlackOne)
                                    else ->
                                        bytes[y * size + x]
                                            .toString()
                                            .toInt()
                                            .toColorInt(isBlackOne)
                                }
                            )
                        }
                    }
                }
            }
            .toFxImg()
    }

fun String.rgb(): Image {
    val colors = lines().filter { it.isNotEmpty() }.map { it.rgbParse() }
    val integers = colors.size.toBigInteger().factor()
    println(integers)
    val root = sqrt(colors.size.toDouble()).toInt().toBigInteger()
    val width =
        integers
            .fold(BigInteger.ONE) { acc, bigInteger ->
                if (acc < root) {
                    acc * bigInteger
                } else {
                    acc
                }
            }
            .toInt()

    val height = colors.size / width
    return colors.fxImage(width, height)
}

fun Image.save() {
    val fileChooser = FileChooser()
    fileChooser.extensionFilters.addAll(
        FileChooser.ExtensionFilter("JPG", "*.jpg"),
        FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
        FileChooser.ExtensionFilter("PNG", "*.png"),
        FileChooser.ExtensionFilter("ALL", "*.*")
    )

    fileChooser.showSaveDialog(null)?.outputStream()?.use {
        it.write(toBufferImage().toByteArray())
    }
}
