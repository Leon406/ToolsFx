package me.leon.view

import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import me.leon.*
import me.leon.ext.math.circleIndex
import me.leon.ext.toFile
import tornadofx.*

class CtfImageEncoderFragment : View("CTF Image Encoder") {

    private val selectedItem = SimpleStringProperty(imageList.first())
    private var iv: ImageView by singleAssign()
    override val root = borderpane {
        prefWidth = 800.0
        prefHeight = 880.0
        top = hbox {
            addClass(Styles.group, Styles.center)
            button(graphic = imageview("/img/back.png")) {
                action {
                    selectedItem.set(
                        imageList[
                            (imageList.indexOf(selectedItem.get()) - 1).circleIndex(imageList.size)]
                    )
                }
            }
            combobox(selectedItem, imageList.toMutableList()) { cellFormat { text = it } }
            button(graphic = imageview("/img/forward.png")) {
                action {
                    selectedItem.set(
                        imageList[
                            (imageList.indexOf(selectedItem.get()) + 1).circleIndex(imageList.size)]
                    )
                }
            }
            selectedItem.addListener { _, _, newValue ->
                println("selectedUrl $newValue")
                iv.image = Image("$CTF_IMG_DIR/$newValue")
            }
        }
        center =
            imageview("$CTF_IMG_DIR/${selectedItem.get()}") {
                this.fitWidth = 560.0
                maxHeight = 800.0
                iv = this
            }
    }

    companion object {
        private const val CTF_IMG_DIR = "/img/ctf"
        private val imageList = mutableListOf<String>()

        init {
            javaClass.getResource(CTF_IMG_DIR)?.toURI()?.toURL()?.file?.run {
                val dir = toFile()
                if (dir.exists()) {
                    dir.listFiles()?.let { imageList.addAll(it.map { it.name }) }
                }
            }
        }
    }
}
