package me.leon.toolsfx.plugin.sample2

import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import me.leon.*
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.toolsfx.plugin.PluginFragment
import tornadofx.*

class Sample2Fragment : PluginFragment("Sample2Fragment") {
    override val version = "v1.0.0"
    override val date: String = "2022-12-15"
    override val author = "Leon406"
    override val description = "Sample2Fragment"
    private var taInput: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private val eventHandler = fileDraggedHandler { taInput.text = it.first().properText() }
    private var locationServiceType: SampleServiceType = SampleServiceType.FUNC_4
    private val controller: Sample2Controller by inject()

    private val inputText: String
        get() = taInput.text.trim()

    private val outputText: String
        get() = taOutput.text

    override val root = vbox {
        prefWidth = 800.0
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        title = "Sample2Fragment"
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
                prefColumns = 7
                togglegroup {
                    serviceTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == SampleServiceType.FUNC_2) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        locationServiceType = new.cast<RadioButton>().text.locationServiceType()
                        println(locationServiceType)
                    }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            paddingLeft = DEFAULT_SPACING

            button(messages["run"], imageview(IMG_RUN)) { action { doProcess() } }
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
        runAsync { controller.process(locationServiceType, inputText) } ui { taOutput.text = it }
    }
}
