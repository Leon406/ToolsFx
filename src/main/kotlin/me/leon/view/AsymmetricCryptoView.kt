package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import me.leon.controller.AsymmetricCryptoController
import me.leon.encode.base.base64
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.DEFAULT_SPACING_10X
import me.leon.ext.clipboardText
import me.leon.ext.copy
import me.leon.ext.fileDraggedHandler
import me.leon.ext.openInBrowser
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
    lateinit var input: TextArea
    lateinit var key: TextArea
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text
    private val info
        get() =
            "RSA  bits: ${selectedBits.get()}  mode: ${if (privateKeyEncrypt.get()) "private key encrypt" 
        else "public key encrypt"} "
    private lateinit var infoLabel: Label
    private val keyText: String
        get() =
            key.text.takeIf { it.contains("-----BEGIN CERTIFICATE") }
                ?: key.text.replace(
                    "-----(?:END|BEGIN) (?:RSA )?\\w+ KEY-----|\n|\r|\r\n".toRegex(),
                    ""
                )

    private var alg = "RSA"
    private var isEncrypt = true
    private val bitsLists = mutableListOf("512", "1024", "2048", "3072", "4096")
    private val selectedBits = SimpleStringProperty("1024")
    private val isPriEncryptOrPubDecrypt
        get() = privateKeyEncrypt.get() && isEncrypt || !privateKeyEncrypt.get() && !isEncrypt

    private val eventHandler = fileDraggedHandler {
        val firstFile = it.first()
        key.text =
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
            button(graphic = imageview(Image("/import.png"))) {
                action { input.text = clipboardText() }
            }
        }
        key =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }

        hbox {
            label(messages["input"]) { tooltip("加密时为明文,解密时为base64编码的密文") }
            button(graphic = imageview(Image("/import.png"))) {
                action { input.text = clipboardText() }
            }
        }
        input =
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
                    isEncrypt = (new as RadioButton).text == messages["encrypt"]
                }
            }

            checkbox(messages["priEncrypt"], privateKeyEncrypt) {
                tooltip("默认公钥加密，私钥解密。开启后私钥加密，公钥解密")
            }

            button(messages["run"], imageview(Image("/run.png"))) { action { doCrypto() } }
            button(messages["genKeypair"]) {
                action { "https://miniu.alipay.com/keytool/create".openInBrowser() }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview(Image("/copy.png"))) { action { outputText.copy() } }
            button(graphic = imageview(Image("/up.png"))) {
                action {
                    input.text = outputText
                    output.text = ""
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

    private fun doCrypto() {
        if (keyText.isEmpty() || inputText.isEmpty()) {
            output.text = "请输入key 或者 待处理内容"
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
                output.text = it
                infoLabel.text = info
            }
    }
}
