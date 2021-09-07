package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
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
    private lateinit var customDict: TextField
    private var enableDict = SimpleBooleanProperty(true)
    private val info: String
        get() =
            " $srcEncodeType --> $dstEncodeType  ${messages["inputLength"]}: ${inputText.length}" +
                "  ${messages["outputLength"]}: ${outputText.length}"
    private val inputText: String
        get() =
            input.text.takeIf {
                isEncode || srcEncodeType in arrayOf(EncodeType.Decimal, EncodeType.Octal)
            }
                ?: input.text.replace("\\s".toRegex(), "")
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
                        enableDict.value = srcEncodeType.type.contains("base")
                        customDict.text = srcEncodeType.dic
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

        hbox {
            label(messages["customDict"])
            alignment = Pos.BASELINE_LEFT
            customDict =
                textfield(srcEncodeType.dic) {
                    enableWhen { enableDict }
                    prefWidth = DEFAULT_SPACING_80X
                }
        }
        tilepane {
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING * 2
            alignment = Pos.CENTER
            button(messages["transfer"], imageview(Image("/run.png"))) {
                action { run() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["up"], imageview(Image("/up.png"))) {
                action {
                    input.text = outputText
                    output.text = ""
                }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["copy"], imageview(Image("/copy.png"))) {
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
        val decode = controller.decode(inputText, srcEncodeType, customDict.text)
        output.text =
            String(decode, Charsets.UTF_8).takeIf { it.contains("解码错误:") }
                ?: controller.encode2String(decode, dstEncodeType)

        infoLabel.text = info
    }
}
