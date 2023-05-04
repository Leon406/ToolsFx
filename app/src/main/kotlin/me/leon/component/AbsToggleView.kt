package me.leon.component

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import me.leon.Styles
import me.leon.ext.cast
import tornadofx.View
import tornadofx.addClass
import tornadofx.radiobutton
import tornadofx.visibleWhen

abstract class AbsToggleView(
    val data: List<String> = listOf("raw", "hex", "base64"),
    var selectedIndex: Int = 0,
    private val visible: BooleanProperty = SimpleBooleanProperty(true)
) : View() {

    val tg: ToggleGroup = ToggleGroup()

    var selectCallback: SelectCallback? = null

    val selectText
        get() = data[selectedIndex]

    fun select(index: Int) {
        tg.selectToggle(tg.toggles[index])
    }

    fun Node.populate() {
        visibleWhen(visible)
        this.addClass(Styles.group)
        data.forEachIndexed { index, s ->
            radiobutton(s, tg) { isSelected = index == selectedIndex }
        }
        tg.selectedToggleProperty().addListener { _, old, new ->
            selectedIndex = data.indexOf(new.cast<RadioButton>().text)
            selectCallback?.onChange(old.cast<RadioButton>().text, new.cast<RadioButton>().text)
        }
    }

    fun callback(callback: SelectCallback) {
        selectCallback = callback
    }

    fun interface SelectCallback {
        fun onChange(old: String, new: String)
    }
}
