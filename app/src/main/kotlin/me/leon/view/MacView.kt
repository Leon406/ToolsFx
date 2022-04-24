package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.Styles
import me.leon.component.KeyIvInputView
import me.leon.controller.MacController
import me.leon.ext.*
import me.leon.ext.crypto.MACs.algorithm
import me.leon.ext.fx.*
import tornadofx.*

class MacView : Fragment("MAC") {
    private val controller: MacController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val enableIv = SimpleBooleanProperty(false)
    private val enableBits = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var labelInfo: Label
    private lateinit var taOutput: TextArea
    private var timeConsumption = 0L
    private var startTime = 0L
    private val inputText: String
        get() = taInput.text
    private var outputText: String
        get() = taOutput.text
        set(value) {
            taOutput.text = value
        }
    private val keyIvInputView = KeyIvInputView(enableIv)

    private var method = "HmacMD5"

    private val regAlgReplace =
        "(POLY1305|GOST3411-2012|SIPHASH(?=\\d-)|SIPHASH128|SHA3(?=\\d{3})|DSTU7564|Skein|Threefish)".toRegex()
    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 10 * 1024 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 10M"
            }
    }

    private val selectedAlgItem = SimpleStringProperty(algorithm.keys.first())
    private val selectedBits = SimpleStringProperty(algorithm.values.first().first())
    private lateinit var cbBits: ComboBox<String>
    private val info
        get() =
            "MAC: $method " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "cost: $timeConsumption ms"
    private var inputEncode = "raw"
    private var outputEncode = "hex"
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            addClass(Styles.left)
            tgInput =
                togglegroup {
                    radiobutton("raw") { isSelected = true }
                    radiobutton("base64")
                    radiobutton("hex")
                    selectedToggleProperty().addListener { _, _, newValue ->
                        inputEncode = newValue.cast<RadioButton>().text
                    }
                }

            button(graphic = imageview("/img/import.png")) {
                action { taInput.text = clipboardText() }
            }
        }
        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlgItem, algorithm.keys.toMutableList())
            label(messages["bits"]) { paddingAll = DEFAULT_SPACING }
            cbBits =
                combobox(selectedBits, algorithm.values.first()) {
                    cellFormat { text = it }
                    enableWhen(enableBits)
                }
        }
        add(keyIvInputView)
        selectedAlgItem.addListener { _, _, newValue ->
            newValue?.run {
                cbBits.items = algorithm[newValue]!!.asObservable()
                selectedBits.set(algorithm[newValue]!!.first())
                enableBits.value = algorithm[newValue]!!.size > 1
                enableIv.value = method.contains("POLY1305|-GMAC|ZUC".toRegex())
            }
        }
        selectedBits.addListener { _, _, newValue ->
            println("selectedBits __ $newValue")
            newValue?.run {
                method =
                    if (selectedAlgItem.get() == "GMAC") "${newValue}-GMAC"
                    else if (selectedAlgItem.get().contains("ZUC-256"))
                        "${selectedAlgItem.get()}-${newValue}"
                    else {
                        "${selectedAlgItem.get()}${
                            newValue.takeIf {
                                algorithm[selectedAlgItem.get()]!!.size > 1
                            } ?: ""
                        }"
                            .replace("SHA2(?!=\\d{3})".toRegex(), "SHA")
                            .replace(regAlgReplace, "$1-")
                    }
                println("算法 $method")
                if (inputText.isNotEmpty() && method.startsWith("Hmac")) {
                    doMac()
                }
            }
        }

        tilepane {
            alignment = Pos.TOP_LEFT
            hgap = DEFAULT_SPACING
            checkbox(messages["singleLine"], isSingleLine)
            button(messages["run"], imageview("/img/run.png")) {
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                action {
                    if (inputText.isNotEmpty()) doMac()
                    else {
                        outputText = ""
                    }
                }
            }
        }

        hbox {
            label(messages["output"])
            addClass(Styles.left)
            tgOutput =
                togglegroup {
                    radiobutton("hex") { isSelected = true }
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        outputEncode = new.cast<RadioButton>().text
                    }
                }
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
        }
        taOutput =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
            }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doMac() =
        runAsync {
            startTime = System.currentTimeMillis()
            if (method.contains("POLY1305|-GMAC|ZUC".toRegex()))
                controller.macWithIv(
                    inputText,
                    keyIvInputView.keyByteArray,
                    keyIvInputView.ivByteArray,
                    method,
                    inputEncode,
                    outputEncode,
                    isSingleLine.get()
                )
            else
                controller.mac(
                    inputText,
                    keyIvInputView.keyByteArray,
                    method,
                    inputEncode,
                    outputEncode,
                    isSingleLine.get()
                )
        } ui
            {
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy)
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
            }
}
