package me.leon.component

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import me.leon.Styles
import me.leon.ext.decodeToByteArray
import me.leon.ext.encodeTo
import tornadofx.*

class KeyIvInputView(
    private val enableIv: SimpleBooleanProperty = SimpleBooleanProperty(true),
    private val enableAssociatedData: SimpleBooleanProperty = SimpleBooleanProperty(false),
    private val autoConvert: SimpleBooleanProperty = SimpleBooleanProperty(false)
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
        toggleKey.callback { old, new ->
            if (autoConvert.get()) {
                tfKey.text = tfKey.text.decodeToByteArray(old).encodeTo(new)
            }
        }
        label("iv:") { visibleWhen(enableIv) }
        tfIv = textfield {
            promptText = messages["ivHint"]
            visibleWhen(enableIv)
        }
        add(toggleIv.root)
        toggleIv.callback { old, new ->
            if (autoConvert.get()) {
                runCatching { tfIv.text = tfIv.text.decodeToByteArray(old).encodeTo(new) }
            }
        }
        label("associateData:") { visibleWhen(enableAssociatedData) }
        tfData = textfield {
            promptText = messages["associateDataHint"]
            visibleWhen(enableAssociatedData)
        }
        add(toggleData.root)
        toggleData.callback { old, new ->
            if (autoConvert.get()) {
                tfData.text = tfData.text.decodeToByteArray(old).encodeTo(new)
            }
        }
    }
}
