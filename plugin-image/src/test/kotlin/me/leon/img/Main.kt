package me.leon.img

import java.io.File
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javax.imageio.ImageIO
import me.leon.ext.fx.toFxImg
import me.leon.toolsfx.plugin.ext.*
import tornadofx.*

val TEST_PRJ_DIR: File = File("").absoluteFile.parentFile
val TEST_DATA_DIR = File(TEST_PRJ_DIR, "testdata/plugin")

class Main : Application() {
    var index = 0

    override fun start(primaryStage: Stage) {
        try {
            val root = BorderPane()
            val image = genImage()

            val imageview = ImageView(image)
            root.center = imageview
            val img = ImageIO.read(File(IMG_DIR, "hand.png"))
            root.bottom =
                Button().button {
                    text = "next"
                    action {
                        println(index % 12)
                        imageview.image =
                            when (index++ % 12) {
                                0 -> img.binary().erode(kernelSize = 2, iteration = 2).toFxImg()
                                1 -> img.binary().erode(kernelSize = 2, iteration = 3).toFxImg()
                                2 -> img.binary().dilate(kernelSize = 2, iteration = 1).toFxImg()
                                3 -> img.binary().dilate(kernelSize = 2, iteration = 2).toFxImg()
                                4 -> img.binary().dilate(kernelSize = 2, iteration = 3).toFxImg()
                                5 -> img.binary().openOp(2).toFxImg()
                                6 -> img.binary().openOp(3).toFxImg()
                                7 -> img.binary().gradient(3).toFxImg()
                                8 -> img.binary().closeOp(2).toFxImg()
                                9 -> img.binary().closeOp(3).toFxImg()
                                10 -> img.binary().blackHat(3).toFxImg()
                                11 -> img.binary().topHat(3).toFxImg()
                                else -> img.mirrorWidth().toFxImg()
                            }
                    }
                }
            val scene = Scene(root, 480.0, 480.0)
            primaryStage.scene = scene
            primaryStage.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun genImage(): Image {
        val input = ImageIO.read(File(IMG_DIR, "capcha2.jpg"))
        //        val image = File(IMG_DIR,"qr01").readText().binaryImage(false)
        //        val image = File(IMG_DIR, "bin2").readText().binaryImage()

        //        val image = File(IMG_DIR, "fix.png").readBytes().fixPng().toImage()
        //        val image = File(IMG_DIR, "base64").readText().base64Image()

        //        val text = File(IMG_DIR, "rgb.txt").readText()
        //        val colors = text.lines().filter { it.isNotEmpty() }.map { it.rgbParse() }
        //        val width = 1296
        //        val height = 154
        //        val image = colors.fxImage(width, height)
        return input.binary().topHat().toFxImg()
    }

    companion object {
        val IMG_DIR = File(TEST_DATA_DIR, "image")
    }
}
