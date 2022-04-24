package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.CHARSETS
import me.leon.Styles
import me.leon.component.KeyIvInputView
import me.leon.controller.SymmetricCryptoController
import me.leon.ext.*
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class SymmetricCryptoStreamView : Fragment(messages["symmetricStream"]) {
    private val controller: SymmetricCryptoController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isFile = SimpleBooleanProperty(false)
    private val isEnableIv = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup
    private var isEncrypt = true
    private lateinit var taOutput: TextArea
    private val inputText: String
        get() = taInput.text
    private val outputText: String
        get() = taOutput.text
    private var timeConsumption = 0L
    private var startTime = 0L
    private val info
        get() =
            "Cipher: $cipher   charset: ${selectedCharset.get()}  file mode: ${isFile.get()} " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "cost: $timeConsumption ms"
    private lateinit var infoLabel: Label
    private val keyIvInputView = KeyIvInputView(isEnableIv)
    private var inputEncode = "raw"
    private var outputEncode = "base64"

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            if (isFile.get())
                it.joinToString(System.lineSeparator(), transform = File::getAbsolutePath)
            else
                with(it.first()) {
                    if (length() <= 128 * 1024)
                        if (realExtension() in unsupportedExts) "unsupported file extension"
                        else readText()
                    else "not support file larger than 128KB, plz use file mode!!!"
                }
    }

    private val algs =
        mutableListOf(
            "RC4", // aka ARC4
            "ChaCha",
            "ChaCha20",
            "ChaCha20-Poly1305",
            "VMPC",
            "VMPC-KSA3",
            "HC128",
            "HC256",
            "Grainv1",
            "Grain128",
            "Salsa20",
            "XSalsa20",
            "Zuc-128",
            "Zuc-256",
        )
    private val selectedAlg = SimpleStringProperty(algs.first())

    private val cipher
        get() = selectedAlg.get()
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())
    private val isSingleLine = SimpleBooleanProperty(false)
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
            combobox(selectedAlg, algs) { cellFormat { text = it } }
            selectedAlg.addListener { _, _, newValue -> isEnableIv.value = newValue != "RC4" }

            label("charset:")
            combobox(selectedCharset, CHARSETS) { cellFormat { text = it } }
        }
        add(keyIvInputView)
        hbox {
            addClass(Styles.center)
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton(messages["encrypt"]) { isSelected = true }
                radiobutton(messages["decrypt"])
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = new.cast<RadioButton>().text == messages["encrypt"]
                    tgOutput.selectToggle(tgOutput.toggles[if (isEncrypt) 1 else 0])
                    if (isEncrypt) tgInput.selectToggle(tgInput.toggles[0])
                    doCrypto()
                }
            }
            checkbox(messages["fileMode"], isFile)
            checkbox(messages["singleLine"], isSingleLine)
            button(messages["run"], imageview("/img/run.png")) {
                enableWhen(!isProcessing)
                action { doCrypto() }
            }
        }
        hbox {
            addClass(Styles.left)
            label(messages["output"])
            tgOutput =
                togglegroup {
                    radiobutton("raw")
                    radiobutton("base64") { isSelected = true }
                    radiobutton("hex")
                    selectedToggleProperty().addListener { _, _, newValue ->
                        println("output ${newValue.cast<RadioButton>().text}")
                        outputEncode = newValue.cast<RadioButton>().text
                    }
                }
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
            button(graphic = imageview("/img/up.png")) {
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                    tgInput.selectToggle(
                        tgInput.toggles[tgOutput.toggles.indexOf(tgOutput.selectedToggle)]
                    )
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
            isProcessing.value = true
            startTime = System.currentTimeMillis()
            if (isEncrypt)
                if (isFile.get())
                    inputText.lineAction2String {
                        controller.encryptByFile(
                            keyIvInputView.keyByteArray,
                            it,
                            keyIvInputView.ivByteArray,
                            cipher
                        )
                    }
                else
                    controller.encrypt(
                        keyIvInputView.keyByteArray,
                        inputText,
                        keyIvInputView.ivByteArray,
                        cipher,
                        selectedCharset.get(),
                        isSingleLine.get(),
                        inputEncode,
                        outputEncode
                    )
            else if (isFile.get())
                inputText.lineAction2String {
                    controller.decryptByFile(
                        keyIvInputView.keyByteArray,
                        it,
                        keyIvInputView.ivByteArray,
                        cipher
                    )
                }
            else
                controller.decrypt(
                    keyIvInputView.keyByteArray,
                    inputText,
                    keyIvInputView.ivByteArray,
                    cipher,
                    selectedCharset.get(),
                    isSingleLine.get(),
                    inputEncode,
                    outputEncode
                )
        } ui
            {
                isProcessing.value = false
                taOutput.text = it
                timeConsumption = System.currentTimeMillis() - startTime
                infoLabel.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
