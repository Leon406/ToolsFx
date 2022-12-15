package me.leon.img

import java.io.File
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import me.leon.ext.fx.toImage
import me.leon.toolsfx.plugin.ext.fixPng

val TEST_PRJ_DIR: File = File("").absoluteFile.parentFile
val TEST_DATA_DIR = File(TEST_PRJ_DIR, "testdata")

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
        //        val image = File(IMG_DIR, "bin2").readText().binaryImage()
        val image = File("E:\\download\\360\\1-raw.png").fixPng().toImage()
        //        val image = File(IMG_DIR, "base64").readText().base64Image()

        //        val text = File(IMG_DIR, "rgb.txt").readText()
        //        val colors = text.lines().filter { it.isNotEmpty() }.map { it.rgbParse() }
        //        val width = 1296
        //        val height = 154
        //        val image = colors.fxImage(width, height)
        return image
    }
    companion object {
        val IMG_DIR = File(TEST_DATA_DIR, "image")
    }
}
