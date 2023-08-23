package me.leon.view

import java.nio.charset.Charset
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import me.leon.*
import me.leon.config.TEXT_AREA_LINES
import me.leon.config.showDict
import me.leon.controller.EncodeController
import me.leon.ext.*
import me.leon.ext.crypto.*
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class EncodeTransferView : Fragment(messages["encodeTransfer"]) {
    private val controller: EncodeController by inject()

    private var timeConsumption = 0L
    private var startTime = 0L
    private var dstEncodeType = EncodeType.URL_ENCODE
    private var srcEncodeType = EncodeType.BASE64

    override val closeable = SimpleBooleanProperty(false)
    private val enableDict = SimpleBooleanProperty(true)
    private val singleLine = SimpleBooleanProperty(false)
    private val selectedSrcCharset = SimpleStringProperty(CHARSETS.first())
    private val selectedDstCharset = SimpleStringProperty(CHARSETS.first())

    private var taInput: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var tfCustomDict: TextField by singleAssign()
    private val info: String
        get() =
            " $srcEncodeType --> $dstEncodeType  ${messages["inputLength"]}: ${inputText.length}" +
                "  ${messages["outputLength"]}: ${outputText.length} cost: $timeConsumption ms"

    private val inputText: String
        get() = taInput.text

    private val outputText: String
        get() = taOutput.text

    private val eventHandler = fileDraggedHandler { taInput.text = it.first().properText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            vbox {
                label(messages["input"])
                combobox(selectedSrcCharset, CHARSETS) { cellFormat { text = it } }
                spacing = DEFAULT_SPACING
                button(graphic = imageview(IMG_NEW_WINDOW)) {
                    tooltip(messages["newWindow"])
                    action { find<EncodeTransferView>().openWindow() }
                }
            }
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            addClass(Styles.left)

            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    encodeTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == EncodeType.BASE64) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        srcEncodeType = new.cast<RadioButton>().text.encodeType()
                        enableDict.value = srcEncodeType.showDict()
                        tfCustomDict.text = srcEncodeType.defaultDict
                    }
                }
            }
        }
        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            prefRowCount = TEXT_AREA_LINES - 2
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
            checkbox(messages["singleLine"], singleLine)
            button(messages["transfer"], imageview(IMG_RUN)) {
                action { run() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["up"], imageview(IMG_UP)) {
                tooltip(messages["up"])
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["copy"], imageview(IMG_COPY)) {
                tooltip(messages["copy"])
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
            addClass(Styles.left)
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    encodeTypeMap
                        .filterNot { it.value == EncodeType.RADIX_N }
                        .forEach {
                            radiobutton(it.key) {
                                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                                if (it.value == EncodeType.URL_ENCODE) isSelected = true
                            }
                        }
                    selectedToggleProperty().addListener { _, _, new ->
                        dstEncodeType = new.cast<RadioButton>().text.encodeType()
                        run()
                    }
                }
            }
        }

        taOutput = textarea {
            vgrow = Priority.ALWAYS
            promptText = messages["outputHint"]
            isWrapText = true
        }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun run() {
        runAsync {
            startTime = System.currentTimeMillis()
            if (singleLine.get()) {
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
            } else {
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
        } ui
            {
                taOutput.text = it
                if (Prefs.autoCopy) {
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
                }
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }
    }
}
