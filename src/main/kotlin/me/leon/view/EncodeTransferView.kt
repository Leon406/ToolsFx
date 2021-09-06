package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import me.leon.controller.EncodeController
import me.leon.ext.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class EncodeTransferView : View(messages["encodeTransfer"]) {
    private val controller: EncodeController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var output: TextArea
    private lateinit var infoLabel: Label
    private val info: String
        get() =
            " $srcEncodeType --> $dstEncodeType  ${messages["inputLength"]}: ${inputText.length}" +
                "  ${messages["outputLength"]}: ${outputText.length}"
    private val inputText: String
        get() = input.text.takeIf { isEncode } ?: input.text.replace("\\s".toRegex(), "")
    private val outputText: String
        get() = output.text

    private var dstEncodeType = EncodeType.UrlEncode
    private var srcEncodeType = EncodeType.Base64
    private var isEncode = true

    private val eventHandler = fileDraggedHandler { input.text = it.first().readText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            label(messages["input"])
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
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

                    selectedToggleProperty().get()
                    selectedToggleProperty().addListener { _, _, new ->
                        srcEncodeType = (new as RadioButton).text.encodeType()
                    }
                }
            }
        }
        input =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }

        tilepane {
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING * 2
            alignment = Pos.CENTER
            button(messages["transfer"], imageview(Image("/run.png"))) {
                action { run() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["up"]) {
                action {
                    input.text = outputText
                    output.text = ""
                }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["copy"]) {
                action { outputText.copy() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
        }
        hbox {
            label(messages["output"])
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                togglegroup {
                    encodeTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == EncodeType.UrlEncode) isSelected = true
                        }
                    }
                    selectedToggleProperty().get()
                    selectedToggleProperty().addListener { _, _, new ->
                        dstEncodeType = (new as RadioButton).text.encodeType()
                        run()
                    }
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
        val decode = controller.decode(inputText, srcEncodeType)
        output.text =
            if (String(decode, Charsets.UTF_8).contains("解码错误:")) {
                String(decode, Charsets.UTF_8)
            } else {
                controller.encode2String(decode, dstEncodeType)
            }
        infoLabel.text = info
    }
}
