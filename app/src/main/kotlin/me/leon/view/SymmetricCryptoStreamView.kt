package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import me.leon.*
import me.leon.component.KeyIvInputView
import me.leon.controller.SymmetricCryptoController
import me.leon.ext.*
import me.leon.ext.crypto.AEAD_MODE_REG
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class SymmetricCryptoStreamView : Fragment(messages["symmetricStream"]) {
    private val controller: SymmetricCryptoController by inject()

    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private var isEncrypt = true
    private var timeConsumption = 0L
    private var startTime = 0L
    private val algs =
        mutableListOf(
            "RC4", // aka ARC4
            "ChaCha",
            "ChaCha20",
            // AEAD
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

    override val closeable = SimpleBooleanProperty(false)
    private val fileProperty = SimpleBooleanProperty(false)
    private val enableIv = SimpleBooleanProperty(false)
    private val autoKeyIv = SimpleBooleanProperty(false)
    private val enableAEAD = SimpleBooleanProperty(false)
    private val processing = SimpleBooleanProperty(false)
    private val selectedAlg = SimpleStringProperty(algs.first())
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())
    private val singleLine = SimpleBooleanProperty(false)

    private lateinit var taInput: TextArea
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup
    private lateinit var taOutput: TextArea
    private lateinit var infoLabel: Label
    private val keyIvInputView = KeyIvInputView(enableIv, enableAEAD, autoKeyIv)

    private val inputText: String
        get() = taInput.text

    private val outputText: String
        get() = taOutput.text

    private val info
        get() =
            "Cipher: $cipher " +
                "Key: ${keyIvInputView.keyByteArray.size} B  " +
                "Charset: ${selectedCharset.get()} " +
                "File: ${fileProperty.get()} " +
                "${messages["inputLength"]}/${messages["outputLength"]}: ${inputText.length}/${outputText.length}  " +
                "Cost: $timeConsumption ms"

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            if (fileProperty.get()) {
                it.joinToString(System.lineSeparator(), transform = File::getAbsolutePath)
            } else {
                it.first().properText(hints = "128KB, plz use file mode!!!")
            }
    }

    private val cipher
        get() = selectedAlg.get()

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            addClass(Styles.left)
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
            checkbox(messages["fileMode"], fileProperty)
        }
        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = eventHandler
        }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, algs) { cellFormat { text = it } }
            selectedAlg.addListener { _, _, newValue ->
                enableIv.value = newValue != "RC4"
                enableAEAD.value = newValue.contains(AEAD_MODE_REG)
            }

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
            checkbox(messages["singleLine"], singleLine)
            checkbox("autoKeyIv", autoKeyIv)
            button(messages["run"], imageview(IMG_RUN)) {
                enableWhen(!processing)
                action { doCrypto() }
            }
        }
        hbox {
            addClass(Styles.left)
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
            vgrow = Priority.ALWAYS
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
            startTime = System.currentTimeMillis()
            runCatching {
                    if (isEncrypt) {
                        if (fileProperty.get()) {
                            inputText.lineAction2String {
                                controller.encryptByFile(
                                    keyIvInputView.keyByteArray,
                                    it,
                                    keyIvInputView.ivByteArray,
                                    cipher,
                                    keyIvInputView.associatedData
                                )
                            }
                        } else {
                            controller.encrypt(
                                keyIvInputView.keyByteArray,
                                inputText,
                                keyIvInputView.ivByteArray,
                                cipher,
                                selectedCharset.get(),
                                singleLine.get(),
                                inputEncode,
                                outputEncode,
                                keyIvInputView.associatedData
                            )
                        }
                    } else if (fileProperty.get()) {
                        inputText.lineAction2String {
                            controller.decryptByFile(
                                keyIvInputView.keyByteArray,
                                it,
                                keyIvInputView.ivByteArray,
                                cipher,
                                keyIvInputView.associatedData
                            )
                        }
                    } else {
                        controller.decrypt(
                            keyIvInputView.keyByteArray,
                            inputText,
                            keyIvInputView.ivByteArray,
                            cipher,
                            selectedCharset.get(),
                            singleLine.get(),
                            inputEncode,
                            outputEncode,
                            keyIvInputView.associatedData
                        )
                    }
                }
                .getOrElse { it.stacktrace() }
        } ui
            {
                processing.value = false
                taOutput.text = it
                timeConsumption = System.currentTimeMillis() - startTime
                infoLabel.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
