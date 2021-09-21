package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.SimpleMsgEvent
import me.leon.controller.ClassicalController
import me.leon.encode.base.base64
import me.leon.ext.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class ClassicalView : View(messages["classical"]) {
    private val controller: ClassicalController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var taOutput: TextArea
    private lateinit var tfParam1: TextField
    private lateinit var tfParam2: TextField
    private lateinit var tfParam3: TextField
    private lateinit var labelInfo: Label
    private val info: String
        get() =
            "${if (isEncrypt) messages["encode"] else messages["decode"]}: $encodeType  ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length}"
    private val inputText: String
        get() = taInput.text
    private val outputText: String
        get() = taOutput.text

    private var encodeType = ClassicalCryptoType.CAESAR
    private var isEncrypt = true

    private val cryptoParams
        get() =
            mutableMapOf(
                "p1" to tfParam1.text,
                "p2" to tfParam2.text,
                "p3" to tfParam3.text,
            )

    private val eventHandler = fileDraggedHandler { taInput.text = it.first().readText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
            button(graphic = imageview("/img/import.png")) {
                action { taInput.text = clipboardText() }
            }
        }

        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
                contextmenu {
                    item(messages["loadFromNet"]) {
                        action { runAsync { inputText.readFromNet() } ui { taInput.text = it } }
                    }
                    item(messages["loadFromNetLoop"]) {
                        action {
                            runAsync { inputText.simpleReadFromNet() } ui { taInput.text = it }
                        }
                    }
                    item(messages["loadFromNet2"]) {
                        action {
                            runAsync { inputText.readBytesFromNet().base64() } ui
                                {
                                    taInput.text = it
                                }
                        }
                    }
                    item(messages["readHeadersFromNet"]) {
                        action {
                            runAsync { inputText.readHeadersFromNet() } ui { taInput.text = it }
                        }
                    }
                }
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label("${messages["encrypt"]}:")
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 6
                togglegroup {
                    classicalTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == ClassicalCryptoType.CAESAR) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        encodeType = new.cast<RadioButton>().text.classicalType()
                        if (isEncrypt) run()
                    }
                }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.BASELINE_CENTER
            tfParam1 = textfield { promptText = "param1" }
            tfParam2 = textfield { promptText = "param2" }
            tfParam3 = textfield { promptText = "param3" }
        }

        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton(messages["encrypt"]) { isSelected = true }
                radiobutton(messages["decrypt"])
                checkbox(messages["singleLine"], isSingleLine)
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = new.cast<RadioButton>().text == messages["encrypt"]
                    run()
                }
            }
            button(messages["run"], imageview("/img/run.png")) { action { run() } }
            button(messages["codeFrequency"]) { action { "https://quipqiup.com/".openInBrowser() } }
        }
        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
            button(graphic = imageview("/img/up.png")) {
                action {
                    taInput.text = outputText
                    taOutput.text = ""
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
            if (isEncrypt)
                controller.encrypt(
                    inputText,
                    encodeType,
                    cryptoParams,
                    isSingleLine.get(),
                )
            else controller.decrypt(inputText, encodeType, cryptoParams, isSingleLine.get())
        if (Prefs.autoCopy) outputText.copy().also { primaryStage.showToast(messages["copied"]) }
        labelInfo.text = info

        fire(SimpleMsgEvent(taOutput.text, 1))
    }
}
