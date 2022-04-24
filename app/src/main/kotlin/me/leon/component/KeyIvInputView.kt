package me.leon.component

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import me.leon.Styles
import me.leon.ext.decodeToByteArray
import tornadofx.*

class KeyIvInputView(
    private val enableIv: SimpleBooleanProperty = SimpleBooleanProperty(true),
    private val enableAssociatedData: SimpleBooleanProperty = SimpleBooleanProperty(false)
) : View() {
    private val toggleKey = ToggleVerticalView()
    private val toggleIv = ToggleVerticalView(show = enableIv)
    private val toggleData = ToggleVerticalView(show = enableAssociatedData)
    private var tfKey: TextField by singleAssign()
    private var tfIv: TextField by singleAssign()
    private var tfData: TextField by singleAssign()
    val keyByteArray
        get() = tfKey.text.decodeToByteArray(toggleKey.selectText)

    val ivByteArray
        get() = tfIv.text.decodeToByteArray(toggleIv.selectText)
    val associatedData
        get() = tfData.text.decodeToByteArray(toggleData.selectText)

    override val root = hbox {
        addClass(Styles.left)
        label("key:")
        tfKey = textfield { promptText = messages["keyHint"] }
        add(toggleKey.root)
        label("iv:") { visibleWhen(enableIv) }
        tfIv =
            textfield {
                promptText = messages["ivHint"]
                visibleWhen(enableIv)
            }
        add(toggleIv.root)
        label("associateData:") { visibleWhen(enableAssociatedData) }
        tfData =
            textfield {
                promptText = messages["associateDataHint"]
                visibleWhen(enableAssociatedData)
            }
        add(toggleData.root)
    }
}
