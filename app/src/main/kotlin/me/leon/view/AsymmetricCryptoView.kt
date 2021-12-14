package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.controller.AsymmetricCryptoController
import me.leon.encode.base.base64
import me.leon.ext.*
import tornadofx.*

class AsymmetricCryptoView : View(FX.messages["asymmetric"]) {
    private val controller: AsymmetricCryptoController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private val privateKeyEncrypt = SimpleBooleanProperty(false)
    lateinit var taInput: TextArea
    lateinit var taKey: TextArea
    lateinit var taOutput: TextArea
    private var inputText: String
        get() = taInput.text
        set(value) {
            taInput.text = value
        }
    private var outputText: String
        get() = taOutput.text
        set(value) {
            taOutput.text = value
        }
    private val info
        get() =
            "RSA  bits: ${selectedBits.get()}  mode: ${
                if (privateKeyEncrypt.get()) "private key encrypt"
                else "public key encrypt"
            } "
    private lateinit var labelInfo: Label
    private var keyText: String
        get() =
            taKey.text.takeIf { it.contains("-----BEGIN CERTIFICATE") }
                ?: taKey.text.replace(
                    "-----(?:END|BEGIN) (?:RSA )?\\w+ KEY-----|\n|\r|\r\n".toRegex(),
                    ""
                )
        set(value) {
            taKey.text = value
        }

    private var alg = "RSA"
    private var isEncrypt = true
    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup
    private val bitsLists = mutableListOf("512", "1024", "2048", "3072", "4096")
    private val selectedBits = SimpleStringProperty("1024")
    private val isPriEncryptOrPubDecrypt
        get() = privateKeyEncrypt.get() && isEncrypt || !privateKeyEncrypt.get() && !isEncrypt

    private val eventHandler = fileDraggedHandler {
        val firstFile = it.first()
        keyText =
            if (firstFile.name.endsWith("pk8")) firstFile.readBytes().base64()
            else
                with(firstFile) {
                    if (length() <= 10 * 1024 * 1024)
                        if (realExtension() in unsupportedExts) "unsupported file extension"
                        else readText()
                    else "not support file larger than 10M"
                }

        with(keyText) {
            val probablyKeySize =
                if (isPriEncryptOrPubDecrypt) this.length * 1.25f else this.length * 5
            println("__ $probablyKeySize")
            val keySize =
                when (probablyKeySize.toInt()) {
                    in 3300..4500 -> 4096
                    in 2600..3300 -> 3072
                    in 1600..2200 -> 2048
                    in 800..1200 -> 1024
                    else -> 512
                }
            selectedBits.set(keySize.toString())
        }
    }
    private val inputEventHandler = fileDraggedHandler {
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
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["input"]) { tooltip("加密时为明文,解密时为base64编码的密文") }
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
                action { inputText = clipboardText() }
            }
        }
        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                prefHeight = DEFAULT_SPACING_16X
                onDragEntered = inputEventHandler
            }

        hbox {
            label(messages["key"])
            button(graphic = imageview("/img/import.png")) { action { keyText = clipboardText() } }
        }
        taKey =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }

        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["bits"])
            combobox(selectedBits, bitsLists) { cellFormat { text = it } }
            togglegroup {
                spacing = DEFAULT_SPACING
                radiobutton(messages["encrypt"]) { isSelected = true }
                radiobutton(messages["decrypt"])
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = new.cast<RadioButton>().text == messages["encrypt"]
                    tgOutput.selectToggle(tgOutput.toggles[if (isEncrypt) 1 else 0])
                    if (isEncrypt) tgInput.selectToggle(tgInput.toggles[0])
                }
            }
            checkbox(messages["singleLine"], isSingleLine)
            checkbox(messages["priEncrypt"], privateKeyEncrypt) {
                tooltip("默认公钥加密，私钥解密。开启后私钥加密，公钥解密")
            }

            button(messages["run"], imageview("/img/run.png")) { action { doCrypto() } }
            button(messages["genKeypair"]) {
                action { "https://miniu.alipay.com/keytool/create".openInBrowser() }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
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
                    inputText = outputText
                    outputText = ""
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
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doCrypto() {
        if (keyText.isEmpty() || inputText.isEmpty()) {
            outputText = "请输入key 或者 待处理内容"
            return
        }

        runAsync {
            if (isEncrypt)
                if (privateKeyEncrypt.get())
                    controller.priEncrypt(
                        keyText,
                        alg,
                        inputText,
                        selectedBits.get().toInt(),
                        isSingleLine.get(),
                        inputEncode = inputEncode,
                        outputEncode = outputEncode
                    )
                else
                    controller.pubEncrypt(
                        keyText,
                        alg,
                        inputText,
                        selectedBits.get().toInt(),
                        isSingleLine.get(),
                        inputEncode = inputEncode,
                        outputEncode = outputEncode
                    )
            else if (privateKeyEncrypt.get())
                controller.pubDecrypt(
                    keyText,
                    alg,
                    inputText,
                    selectedBits.get().toInt(),
                    isSingleLine.get(),
                    inputEncode,
                    outputEncode
                )
            else
                controller.priDecrypt(
                    keyText,
                    alg,
                    inputText,
                    selectedBits.get().toInt(),
                    isSingleLine.get(),
                    inputEncode,
                    outputEncode
                )
        } ui
            {
                outputText = it
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
