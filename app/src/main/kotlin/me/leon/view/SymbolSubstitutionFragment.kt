package me.leon.view

import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Paths
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlin.io.path.listDirectoryEntries
import me.leon.*
import me.leon.ext.math.circleIndex
import tornadofx.*

class SymbolSubstitutionFragment : View("Symbol Substitution") {

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
            val uri = SymbolSubstitutionFragment::class.java.getResource(CTF_IMG_DIR).toURI()
            val dirPath =
                try {
                    Paths.get(uri)
                } catch (ignore: FileSystemNotFoundException) {
                    // If this is thrown, then it means that we are running the JAR directly
                    // (example: not from an IDE)
                    println("read resource from jar file")
                    val env = mutableMapOf<String, String>()
                    FileSystems.newFileSystem(uri, env).getPath(CTF_IMG_DIR)
                }
            imageList.addAll(dirPath.listDirectoryEntries().map { it.fileName.toString() }.sorted())
        }
    }
}
