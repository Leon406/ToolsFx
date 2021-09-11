package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import me.leon.controller.AsymmetricCryptoController
import me.leon.encode.base.base64
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.DEFAULT_SPACING_10X
import me.leon.ext.Prefs
import me.leon.ext.cast
import me.leon.ext.clipboardText
import me.leon.ext.copy
import me.leon.ext.fileDraggedHandler
import me.leon.ext.openInBrowser
import me.leon.ext.showToast
import tornadofx.FX
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.checkbox
import tornadofx.combobox
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.radiobutton
import tornadofx.textarea
import tornadofx.togglegroup
import tornadofx.tooltip
import tornadofx.vbox

class AsymmetricCryptoView : View(FX.messages["asymmetric"]) {
    private val controller: AsymmetricCryptoController by inject()
    override val closeable = SimpleBooleanProperty(false)
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
    private val bitsLists = mutableListOf("512", "1024", "2048", "3072", "4096")
    private val selectedBits = SimpleStringProperty("1024")
    private val isPriEncryptOrPubDecrypt
        get() = privateKeyEncrypt.get() && isEncrypt || !privateKeyEncrypt.get() && !isEncrypt

    private val eventHandler = fileDraggedHandler {
        val firstFile = it.first()
        keyText =
            if (firstFile.name.endsWith("pk8")) firstFile.readBytes().base64()
            else firstFile.readText()

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
    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
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
            label(messages["input"]) { tooltip("加密时为明文,解密时为base64编码的密文") }
            button(graphic = imageview("/img/import.png")) {
                action { inputText = clipboardText() }
            }
        }
        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                prefHeight = DEFAULT_SPACING_10X
            }

        hbox {
            alignment = Pos.CENTER_LEFT
            label(messages["bits"])
            combobox(selectedBits, bitsLists) { cellFormat { text = it } }
            togglegroup {
                spacing = DEFAULT_SPACING
                radiobutton(messages["encrypt"]) { isSelected = true }
                radiobutton(messages["decrypt"])
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = new.cast<RadioButton>().text == messages["encrypt"]
                }
            }

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
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
            button(graphic = imageview("/img/up.png")) {
                action {
                    inputText = outputText
                    outputText = ""
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
                    controller.priEncrypt(keyText, alg, inputText, selectedBits.get().toInt())
                else controller.pubEncrypt(keyText, alg, inputText, selectedBits.get().toInt())
            else if (privateKeyEncrypt.get())
                controller.pubDecrypt(keyText, alg, inputText, selectedBits.get().toInt())
            else controller.priDecrypt(keyText, alg, inputText, selectedBits.get().toInt())
        } ui
            {
                outputText = it
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
