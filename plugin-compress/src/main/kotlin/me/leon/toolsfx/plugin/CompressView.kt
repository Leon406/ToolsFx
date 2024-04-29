package me.leon.toolsfx.plugin

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.*
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.toolsfx.plugin.compress.compressType
import me.leon.toolsfx.plugin.compress.compressTypeMap
import tornadofx.*
import tornadofx.FX.Companion.messages

class CompressView : PluginFragment(messages["compression"]) {
    override val version = "v1.2.1"
    override val date: String = "2024-04-29"
    override val author = "Leon406"
    override val description: String = FX.messages["compression"]

    init {
        println("Plugin Info:$description $version $date $author  ")
    }

    private val controller: CompressController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val processing = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup
    private var isCompress = true
    private lateinit var taOutput: TextArea
    private val inputText: String
        get() = taInput.text

    private val outputText: String
        get() = taOutput.text

    private val info
        get() = "Cipher: $cipher   charset: ${selectedCharset.get()} "

    private lateinit var infoLabel: Label

    private var inputEncode = "raw"
    private var outputEncode = "base64"

    private val eventHandler = fileDraggedHandler { taInput.text = it.first().properText() }

    private val algs = compressTypeMap.values.map { it.alg }
    private val selectedAlg = SimpleStringProperty(algs[2])

    private val cipher
        get() = selectedAlg.get()

    private val selectedCharset = SimpleStringProperty(CHARSETS.first())
    private val singleLine = SimpleBooleanProperty(false)
    private val centerNode = vbox {
        title = messages["compression"]
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            tgInput = togglegroup {
                radiobutton("raw") { isSelected = true }
                radiobutton("base64")
                radiobutton("hex")
                selectedToggleProperty().addListener { _, _, newValue ->
                    inputEncode = newValue.cast<RadioButton>().text
                }
            }

            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taInput.text = clipboardText() }
            }
        }
        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = eventHandler
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label(messages["alg"])
            tilepane {
                vgap = 8.0
                hgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 5
                togglegroup {
                    compressTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == selectedAlg.get().compressType()) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        selectedAlg.set(new.cast<RadioButton>().text)
                    }
                }
            }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton(messages["compress"]) { isSelected = true }
                radiobutton(messages["decompress"])
                selectedToggleProperty().addListener { _, _, new ->
                    isCompress = new.cast<RadioButton>().text == messages["compress"]
                    tgOutput.selectToggle(tgOutput.toggles[if (isCompress) 1 else 0])
                    if (isCompress) tgInput.selectToggle(tgInput.toggles[0])
                    doCrypto()
                }
            }
            checkbox(messages["singleLine"], singleLine)
            button(messages["run"], imageview(IMG_RUN)) {
                enableWhen(!processing)
                action { doCrypto() }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["output"])
            tgOutput = togglegroup {
                radiobutton("raw")
                radiobutton("base64") { isSelected = true }
                radiobutton("hex")
                selectedToggleProperty().addListener { _, _, newValue ->
                    println("output ${newValue.cast<RadioButton>().text}")
                    outputEncode = newValue.cast<RadioButton>().text
                }
            }
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { outputText.copy() }
            }
            button(graphic = imageview(IMG_UP)) {
                tooltip(messages["up"])
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                    tgInput.selectToggle(
                        tgInput.toggles[tgOutput.toggles.indexOf(tgOutput.selectedToggle)]
                    )
                }
            }
        }
        taOutput = textarea {
            promptText = messages["outputHint"]
            isWrapText = true
        }
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { infoLabel = label(info) }
    }

    private fun doCrypto() {
        runAsync {
            processing.value = true
            if (isCompress) {
                controller.compress(
                    inputText,
                    cipher.compressType(),
                    inputEncode,
                    outputEncode,
                    singleLine.get(),
                )
            } else {
                controller.decompress(
                    inputText,
                    cipher.compressType(),
                    inputEncode,
                    outputEncode,
                    singleLine.get(),
                )
            }
        } ui
            {
                processing.value = false
                taOutput.text = it
                infoLabel.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
