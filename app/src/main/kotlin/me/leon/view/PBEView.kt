package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.CHARSETS
import me.leon.Styles
import me.leon.controller.PBEController
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.PBE
import me.leon.ext.fx.*
import tornadofx.*

class PBEView : Fragment("PBE") {
    private val controller: PBEController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)

    private val isProcessing = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var tfPwd: TextField
    private lateinit var tfIteration: TextField
    private lateinit var tfKeyLength: TextField
    private lateinit var tfSalt: TextField
    private lateinit var tfSaltLength: TextField
    private lateinit var tgGroup: ToggleGroup
    private var isEncrypt = true
    private lateinit var taOutput: TextArea
    private val inputText: String
        get() = taInput.text
    private val outputText: String
        get() = taOutput.text
    private val keyLength
        get() = tfKeyLength.text.toInt()
    private val saltLength
        get() = tfSaltLength.text.toInt()
    private var timeConsumption = 0L
    private var startTime = 0L
    private val info
        get() =
            "PBE Cipher: $cipher   charset: ${selectedCharset.get()} " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "cost: $timeConsumption ms"
    private lateinit var infoLabel: Label

    private var saltEncode = "hex"

    private var saltByteArray
        get() =
            if (tfSalt.text.isEmpty() && isEncrypt)
                controller.getSalt(saltLength).also {
                    tfSalt.text = it.toHex()
                    tgGroup.selectToggle(
                        tgGroup.toggles.first { it.cast<RadioButton>().text == "hex" }
                    )
                }
            else tfSalt.text.decodeToByteArray(saltEncode)
        set(value) {
            tfSalt.text = value.encodeTo(saltEncode)
        }

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 128 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 128KB"
            }
    }

    private val algs = PBE.PBE_CRYPTO.toList()

    private val selectedAlg = SimpleStringProperty(algs.first())

    private val cipher
        get() = "PBEWith${selectedAlg.get()}"
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())

    private val centerNode = vbox {
        addClass(Styles.group)
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
            }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, algs) { cellFormat { text = it } }

            label("charset:")
            combobox(selectedCharset, CHARSETS) { cellFormat { text = it } }
        }
        hbox {
            addClass(Styles.left)
            label(messages["pwd"])
            tfPwd = textfield { promptText = messages["pwdHintNull"] }

            label(messages["keyLen"])
            tfKeyLength =
                textfield("128") {
                    prefWidth = DEFAULT_SPACING_8X
                    textFormatter = intTextFormatter
                }
            label(messages["saltLen"])
            tfSaltLength =
                textfield("8") {
                    prefWidth = DEFAULT_SPACING_8X
                    textFormatter = intTextFormatter
                }
            label("iteration:")
            tfIteration =
                textfield("1") {
                    prefWidth = DEFAULT_SPACING_8X
                    textFormatter = intTextFormatter
                }
            label("salt:")
            tfSalt = textfield { promptText = "optional,可空" }
            vbox {
                addClass(Styles.group)
                tgGroup =
                    togglegroup {
                        radiobutton("hex") { isSelected = true }
                        radiobutton("base64")
                        radiobutton("raw")
                        selectedToggleProperty().addListener { _, _, new ->
                            saltEncode = new.cast<RadioButton>().text
                        }
                    }
            }
        }
        hbox {
            addClass(Styles.center)
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton(messages["encrypt"]) { isSelected = true }
                radiobutton(messages["decrypt"])
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = new.cast<RadioButton>().text == messages["encrypt"]
                    doCrypto()
                }
            }

            checkbox(messages["singleLine"], isSingleLine)
            button("generate salt", imageview("/img/run.png")) {
                action { controller.getSalt(saltLength).also { saltByteArray = it } }
            }
            button(messages["run"], imageview("/img/run.png")) {
                enableWhen(!isProcessing)
                action { doCrypto() }
            }
        }
        hbox {
            label(messages["output"])
            spacing = DEFAULT_SPACING
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
        bottom = hbox { infoLabel = label(info) }
    }

    private fun doCrypto() {
        runAsync {
            startTime = System.currentTimeMillis()
            if (taInput.text.isEmpty()) return@runAsync ""
            isProcessing.value = true
            runCatching {
                if (isEncrypt)
                    controller.encrypt(
                        tfPwd.text,
                        inputText,
                        saltByteArray,
                        cipher,
                        tfIteration.text.toInt(),
                        keyLength,
                        isSingleLine.get()
                    )
                else {

                    saltByteArray = inputText.base64Decode().sliceArray(8 until (8 + saltLength))
                    controller.decrypt(
                        tfPwd.text,
                        inputText,
                        saltLength,
                        cipher,
                        tfIteration.text.toInt(),
                        keyLength,
                        isSingleLine.get()
                    )
                }
            }
                .getOrElse { it.stacktrace() }
        } ui
            {
                isProcessing.value = false
                taOutput.text =
                    it.also {
                        if (it.startsWith("U2FsdGVk"))
                            saltByteArray = it.base64Decode().sliceArray(8 until (8 + saltLength))
                    }
                timeConsumption = System.currentTimeMillis() - startTime
                infoLabel.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
