package me.leon.view

import javafx.beans.property.*
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
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
    private var param1: TextField by singleAssign()
    private var cbParam: ComboBox<String> by singleAssign()

    private val eventHandler = fileDraggedHandler { taInput.text = it.first().properText() }
    private var serviceType: MiscServiceType = MiscServiceType.UUID
    private val controller: MiscController by inject()

    private val processing: BooleanProperty = SimpleBooleanProperty(false)
    private val showComboParam = SimpleBooleanProperty(false)
    private val selectedParam = SimpleStringProperty("")
    private val showParams = SimpleBooleanProperty(false)

    private val inputText: String
        get() = taInput.text.trim()

    private val outputText: String
        get() = taOutput.text

    private val paramsMap: Map<String, String>
        get() = mutableMapOf(P1 to param1.text, C1 to selectedParam.get())

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
                prefColumns = 7
                togglegroup {
                    miscServiceTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == MiscServiceType.UUID) {
                                isSelected = true
                            }
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        serviceType = new.cast<RadioButton>().text.miscServiceType()
                        taInput.promptText = serviceType.hint()
                        val paramHints = serviceType.paramsHints()
                        showParams.value = paramHints.isNotEmpty()
                        param1.promptText = "".takeIf { paramHints.isEmpty() } ?: paramHints.first()
                        serviceType.displayOptions()
                        println("$serviceType params ${paramHints.contentToString()}")
                    }
                }
            }
        }
        hbox {
            addClass(Styles.group, Styles.left)
            cbParam = combobox(selectedParam) { visibleWhen(showComboParam) }
            param1 = textfield {
                prefWidth = DEFAULT_SPACING_24X
                visibleWhen(showParams)
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
            vgrow = Priority.ALWAYS
            promptText = messages["outputHint"]
            isWrapText = true
        }
        MiscServiceType.UUID.displayOptions()
    }

    private fun MiscServiceType.displayOptions() {
        val options = options()
        showComboParam.value = options.isNotEmpty()
        if (options.isNotEmpty()) {
            cbParam.items = options.toMutableList().asObservable()
            selectedParam.set(if (options.isEmpty()) "" else options.first())
            cbParam.bind(selectedParam)
        }
    }

    private fun doProcess() {
        if (inputText.isEmpty() && serviceType != MiscServiceType.IP_LOCATION) return
        runAsync {
            processing.value = true
            controller.process(serviceType, inputText, paramsMap)
        } ui
            {
                processing.value = false
                taOutput.text = it
            }
    }
}
