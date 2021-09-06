package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import me.leon.base.base64
import me.leon.controller.EncodeController
import me.leon.ext.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class EncodeView : View(messages["encodeAndDecode"]) {
    private val controller: EncodeController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var output: TextArea
    private lateinit var infoLabel: Label
    private lateinit var customDict: TextField
    private var enableDict = SimpleBooleanProperty(true)
    private val info: String
        get() =
            "${if (isEncode) messages["encode"] else messages["decode"]}: $encodeType  ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length}"
    private val inputText: String
        get() = input.text.takeIf { isEncode } ?: input.text.replace("\\s".toRegex(), "")
    private val outputText: String
        get() = output.text

    private var encodeType = EncodeType.Base64
    private var isEncode = true

    private val eventHandler = fileDraggedHandler { input.text = it.first().readText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
            button(graphic = imageview(Image("/import.png"))) {
                action { input.text = clipboardText() }
            }
        }

        input =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
                contextmenu {
                    item(messages["loadFromNet"]) {
                        action { runAsync { inputText.readFromNet() } ui { input.text = it } }
                    }
                    item(messages["loadFromNet2"]) {
                        action {
                            runAsync { inputText.readBytesFromNet().base64() } ui
                                {
                                    input.text = it
                                }
                        }
                    }
                }
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label("${messages["encode"]}:")
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                togglegroup {
                    encodeTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == EncodeType.Base64) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        encodeType = (new as RadioButton).text.encodeType()
                        enableDict.value = encodeType.type.contains("base")
                        customDict.text = encodeType.dic
                        if (isEncode) {
                            output.text =
                                controller.encode2String(inputText, encodeType, customDict.text)
                            infoLabel.text = info
                        }
                    }
                }
            }
        }

        hbox {
            label(messages["customDict"])
            alignment = Pos.BASELINE_LEFT
            customDict =
                textfield(encodeType.dic) {
                    enableWhen { enableDict }
                    prefWidth = DEFAULT_SPACING_80X
                }
        }

        hbox {
            spacing = DEFAULT_SPACING
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton(messages["encode"]) { isSelected = true }
                radiobutton(messages["decode"])
                selectedToggleProperty().addListener { _, _, new ->
                    isEncode = (new as RadioButton).text == messages["encode"]
                    run()
                }
            }
            button(messages["run"], imageview(Image("/run.png"))) { action { run() } }
        }
        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview(Image("/copy.png"))) { action { outputText.copy() } }
            button(graphic = imageview(Image("/up.png"))) {
                action {
                    input.text = outputText
                    output.text = ""
                }
            }
        }

        output =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
            }
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { infoLabel = label(info) }
    }

    private fun run() {
        if (isEncode) {
            output.text = controller.encode2String(inputText, encodeType, customDict.text)
        } else {
            output.text = controller.decode2String(inputText, encodeType, customDict.text)
        }
        infoLabel.text = info
    }
}
