package me.leon.view

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import me.leon.*
import me.leon.controller.MiscController
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.misc.*
import me.leon.toolsfx.plugin.PluginFragment
import tornadofx.*

class MiscFragment : PluginFragment("Misc") {
    override val version = "v1.0.0"
    override val date: String = "2023-01-31"
    override val author = "Leon406"
    override val description = "Misc"
    private var taInput: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private val eventHandler = fileDraggedHandler { taInput.text = it.first().properText() }
    private var serviceType: MiscServiceType = MiscServiceType.UUID
    private val controller: MiscController by inject()
    private val processing: BooleanProperty = SimpleBooleanProperty(false)

    private val inputText: String
        get() = taInput.text.trim()
    private val outputText: String
        get() = taOutput.text

    override val root = vbox {
        prefWidth = 800.0
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        title = "Misc"
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            label(messages["input"])
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taInput.text = clipboardText() }
            }
        }

        taInput = textarea {
            isWrapText = true
            onDragEntered = eventHandler
            promptText = serviceType.hint()
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label("function:")
            tilepane {
                vgap = 8.0
                hgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 5
                togglegroup {
                    miscServiceTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == MiscServiceType.UUID) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        serviceType = new.cast<RadioButton>().text.miscServiceType()
                        taInput.promptText = serviceType.hint()
                        println(serviceType)
                    }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            paddingLeft = DEFAULT_SPACING

            button(messages["run"], imageview(IMG_RUN)) {
                enableWhen(!processing)
                action { doProcess() }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["output"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { outputText.copy() }
            }
            button(graphic = imageview(IMG_UP)) {
                tooltip(messages["up"])
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                }
            }
        }
        taOutput = textarea {
            promptText = messages["outputHint"]
            isWrapText = true
        }
    }

    private fun doProcess() {
        if (inputText.isEmpty()) return
        runAsync {
            processing.value = true
            controller.process(serviceType, inputText)
        } ui
            {
                processing.value = false
                taOutput.text = it
            }
    }
}
