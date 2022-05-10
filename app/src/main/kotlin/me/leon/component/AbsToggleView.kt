package me.leon.component

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import me.leon.Styles
import me.leon.ext.cast
import tornadofx.*

abstract class AbsToggleView(
    val data: List<String> = listOf("raw", "hex", "base64"),
    var selectedIndex: Int = 0,
    private val visible: BooleanProperty = SimpleBooleanProperty(true)
) : View() {

    private var tg: ToggleGroup = ToggleGroup()

    fun select(index: Int) {
        tg.selectToggle(tg.toggles[index])
    }

    val selectText
        get() = data[selectedIndex]

    fun Node.populate() {
        visibleWhen(visible)
        this.addClass(Styles.group)
        data.forEachIndexed { index, s ->
            radiobutton(s, tg) { isSelected = index == selectedIndex }
        }
        tg.selectedToggleProperty().addListener { _, _, new ->
            selectedIndex = data.indexOf(new.cast<RadioButton>().text)
        }
    }
}
