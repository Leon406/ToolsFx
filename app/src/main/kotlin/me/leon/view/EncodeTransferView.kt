package me.leon.view

import java.nio.charset.Charset
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.CHARSETS
import me.leon.controller.EncodeController
import me.leon.ext.*
import me.leon.ext.crypto.EncodeType
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class EncodeTransferView : View(messages["encodeTransfer"]) {
    private val controller: EncodeController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var taOutput: TextArea
    private lateinit var labelInfo: Label
    private lateinit var tfCustomDict: TextField
    private var enableDict = SimpleBooleanProperty(true)
    private val isSingleLine = SimpleBooleanProperty(false)
    private val info: String
        get() =
            " $srcEncodeType --> $dstEncodeType  ${messages["inputLength"]}: ${inputText.length}" +
                "  ${messages["outputLength"]}: ${outputText.length}"
    private val inputText: String
        get() =
            taInput.text.takeIf {
                isEncode || srcEncodeType in arrayOf(EncodeType.Decimal, EncodeType.Octal)
            }
                ?: taInput.text.stripAllSpace()
    private val outputText: String
        get() = taOutput.text

    private var dstEncodeType = EncodeType.UrlEncode
    private var srcEncodeType = EncodeType.Base64
    private var isEncode = true
    private val selectedSrcCharset = SimpleStringProperty(CHARSETS.first())
    private val selectedDstCharset = SimpleStringProperty(CHARSETS.first())

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 10 * 1024 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 10M"
            }
    }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            vbox {
                label(messages["input"])
                combobox(selectedSrcCharset, CHARSETS) { cellFormat { text = it } }
            }
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING

            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    encodeTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == EncodeType.Base64) isSelected = true
                        }
                    }

                    selectedToggleProperty().get()
                    selectedToggleProperty().addListener { _, _, new ->
                        srcEncodeType = new.cast<RadioButton>().text.encodeType()
                        enableDict.value =
                            srcEncodeType.type.contains("base") && srcEncodeType.type != "base100"
                        tfCustomDict.text = srcEncodeType.defaultDict
                    }
                }
            }
        }
        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
                textProperty().addListener { _, _, _ -> labelInfo.text = info }
            }

        hbox {
            label(messages["customDict"])
            alignment = Pos.BASELINE_LEFT
            tfCustomDict =
                textfield(srcEncodeType.defaultDict) {
                    enableWhen { enableDict }
                    prefWidth = DEFAULT_SPACING_80X
                }
        }
        tilepane {
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING * 2
            alignment = Pos.CENTER
            checkbox(messages["singleLine"], isSingleLine)
            button(messages["transfer"], imageview("/img/run.png")) {
                action { run() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["up"], imageview("/img/up.png")) {
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["copy"], imageview("/img/copy.png")) {
                action { outputText.copy() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
        }
        hbox {
            vbox {
                label(messages["output"])
                combobox(selectedDstCharset, CHARSETS) { cellFormat { text = it } }
            }

            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    encodeTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == EncodeType.UrlEncode) isSelected = true
                        }
                    }
                    selectedToggleProperty().get()
                    selectedToggleProperty().addListener { _, _, new ->
                        dstEncodeType = new.cast<RadioButton>().text.encodeType()
                        run()
                    }
                }
            }
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

    private fun run() {
        taOutput.text =
            if (isSingleLine.get())
                inputText.lineAction2String {
                    val decode =
                        controller.decode(
                            it,
                            srcEncodeType,
                            tfCustomDict.text,
                            selectedSrcCharset.get()
                        )
                    val encodeString = decode.toString(Charset.forName(selectedSrcCharset.get()))
                    println("transfer $encodeString")
                    controller.encode2String(decode, dstEncodeType, "", selectedDstCharset.get())
                }
            else {
                val decode =
                    controller.decode(
                        inputText,
                        srcEncodeType,
                        tfCustomDict.text,
                        selectedSrcCharset.get()
                    )
                val encodeString = decode.toString(Charset.forName(selectedSrcCharset.get()))
                println("transfer $encodeString")
                decode.toString(Charsets.UTF_8).takeIf { it.contains("解码错误:") }
                    ?: controller.encode2String(decode, dstEncodeType, "", selectedDstCharset.get())
            }
        if (Prefs.autoCopy) outputText.copy().also { primaryStage.showToast(messages["copied"]) }
        labelInfo.text = info
    }
}
