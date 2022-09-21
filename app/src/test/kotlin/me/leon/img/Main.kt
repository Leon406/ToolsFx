package me.leon.img

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import kotlin.math.sqrt
import me.leon.TEST_DATA_DIR
import me.leon.encode.base.base64Decode
import me.leon.ext.fx.toFxImg

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

fun String.base64Image() = Image(ByteArrayInputStream(base64Decode()))

fun Int.toColorInt(isBlackOne: Boolean = true) =
    when {
        isBlackOne && this == 0 -> 0xFFFFFF
        !isBlackOne && this == 1 -> 0xFFFFFF
        else -> 0
    }

fun String.binaryImage(isNormal: Boolean = true) =
    with(sqrt(length.toDouble())) {
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
                                    isNormal -> bytes[y * size + x].toString().toInt().toColorInt()
                                    x < 7 && y < 7 -> QR_MARK[x][y].toColorInt()
                                    x > size - 8 && y < 7 -> QR_MARK[x - (size - 7)][y].toColorInt()
                                    x < 7 && y > size - 8 -> QR_MARK[x][y - (size - 7)].toColorInt()
                                    else -> bytes[y * size + x].toString().toInt().toColorInt()
                                }
                            )
                        }
                    }
                }
            }
            .toFxImg()
    }

class Main : Application() {
    override fun start(primaryStage: Stage) {
        try {
            val root = BorderPane()
            val image = genImage()

            val imageview = ImageView(image)
            root.center = imageview
            val scene = Scene(root, 480.0, 480.0)
            primaryStage.scene = scene
            primaryStage.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun genImage(): Image {

        //        val image = File(IMG_DIR,"qr01").readText().binaryImage(false)
        //        val image = File(IMG_DIR,"bin").readText().binaryImage()
        val image = File(IMG_DIR, "base64").readText().base64Image()

        return image
    }

    companion object {
        val IMG_DIR = File(TEST_DATA_DIR, "image")
    }
}
