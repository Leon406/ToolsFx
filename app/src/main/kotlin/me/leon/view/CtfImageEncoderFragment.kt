package me.leon.view

import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import me.leon.*
import me.leon.ext.math.circleIndex
import tornadofx.*

class CtfImageEncoderFragment : View("CTF Image Encoder") {
    private val imageList =
        listOf(
            "alien",
            "alien2",
            "cligon",
            "covenant",
            "dancing_man",
            "dothraki",
            "dual_flag",
            "egypt",
            "egypt2",
            "fanfan",
            "galaxy",
            "hexahue",
            "international_signal_flag",
            "musical",
            "musical2",
            "pigpen",
            "pigpen2",
            "pigpen3",
            "shaduo",
            "shenqibaobei",
            "sierda",
            "simulation_life",
            "templarsCipher",
            "unknown"
        )

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
                iv.image = Image("/img/ctf/$newValue.jpg")
            }
        }
        center =
            imageview("/img/ctf/${selectedItem.get()}.jpg") {
                this.fitWidth = 560.0
                maxHeight = 800.0
                iv = this
            }
    }
}
