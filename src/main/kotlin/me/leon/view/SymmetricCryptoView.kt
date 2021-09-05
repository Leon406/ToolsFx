package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import me.leon.base.base64Decode
import me.leon.controller.SymmetricCryptoController
import me.leon.ext.*
import tornadofx.*

class SymmetricCryptoView : View("对称加密(block)") {
    private val controller: SymmetricCryptoController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isFile = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var key: TextField
    private lateinit var iv: TextField
    private var isEncrypt = true
    private lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text
    private val keyByteArray
        get() =
            when (keyEncode) {
                "raw" -> key.text.toByteArray()
                "hex" -> key.text.hex2ByteArray()
                "base64" -> key.text.base64Decode()
                else -> byteArrayOf()
            }

    private var keyEncode = "raw"
    private var ivEncode = "raw"

    private val ivByteArray
        get() =
            when (ivEncode) {
                "raw" -> iv.text.toByteArray()
                "hex" -> iv.text.hex2ByteArray()
                "base64" -> iv.text.base64Decode()
                else -> byteArrayOf()
            }

    private val eventHandler = fileDraggedHandler {
        input.text =
            if (isFile.get()) it.joinToString(System.lineSeparator()) { it.absolutePath }
            else it.first().readText()
    }
    private val algs =
        mutableListOf(
            "DES",
            "DESEDE",
            "AES",
            "SM4",
            "Blowfish",
            "Twofish",
            "Threefish-256",
            "Threefish-512",
            "Threefish-1024",
            "RC2",
            "RC5",
            "RC6",
            "Camellia",
            "CAST5",
            "CAST6",
            "ARIA",
            "Skipjack",
            "Serpent",
            "DSTU7624",
            "IDEA",
            "SEED",
            "TEA",
            "XTEA",
        )
    private val paddingsAlg =
        mutableListOf(
            "PKCS5Padding",
            "PKCS7Padding",
            "ISO10126Padding",
            "ZeroBytePadding",
            "NoPadding",
            "TBCPadding",
            "X923Padding",
            "ISO7816d4Padding",
            "ISO10126d2Padding"
        )
    private val modes = mutableListOf("CBC", "ECB", "CFB", "OFB", "CTR", "GCM", "CCM", "EAX", "OCB")
    private val selectedAlg = SimpleStringProperty(algs[2])
    private val charsets = mutableListOf("UTF-8", "GBK", "GB2312", "GB18030", "ISO-8859-1", "BIG5")
    private val selectedPadding = SimpleStringProperty(paddingsAlg.first())
    private val selectedCharset = SimpleStringProperty(charsets.first())
    private val selectedMod = SimpleStringProperty(modes.first())

    private val cipher
        get() = "${selectedAlg.get()}/${selectedMod.get()}/${selectedPadding.get()}"

    override val root = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label("待处理:")
            button("剪贴板导入") { action { input.text = clipboardText() } }
        }
        input =
            textarea {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            label("算法:")
            combobox(selectedAlg, algs) { cellFormat { text = it } }
            label("mode:")
            combobox(selectedMod, modes) { cellFormat { text = it } }
            label("padding:")
            combobox(selectedPadding, paddingsAlg) { cellFormat { text = it } }
            label("charset:")
            combobox(selectedCharset, charsets) { cellFormat { text = it } }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            label("key:")
            key = textfield { promptText = "请输入key" }
            vbox {
                togglegroup {
                    spacing = DEFAULT_SPACING
                    paddingAll = DEFAULT_SPACING
                    radiobutton("raw") { isSelected = true }
                    radiobutton("hex")
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        keyEncode = (new as RadioButton).text
                    }
                }
            }
            label("iv:")
            iv = textfield { promptText = "请输入iv" }
            vbox {
                togglegroup {
                    spacing = DEFAULT_SPACING
                    paddingAll = DEFAULT_SPACING
                    radiobutton("raw") { isSelected = true }
                    radiobutton("hex")
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        ivEncode = (new as RadioButton).text
                    }
                }
            }
        }
        selectedAlg.addListener { _, _, newValue -> newValue?.run { println("alg $newValue") } }

        hbox {
            alignment = Pos.CENTER_LEFT
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton("加密") { isSelected = true }
                radiobutton("解密")
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = (new as RadioButton).text == "加密"
                    doCrypto()
                }
            }
            checkbox("文件模式", isFile)
            button("运行") {
                enableWhen(!isProcessing)
                action { doCrypto() }
            }
            button("上移") {
                action {
                    input.text = outputText
                    output.text = ""
                }
            }
        }
        hbox {
            label("输出内容:")
            button(graphic = imageview(Image("/copy.png"))) { action { outputText.copy() } }
        }
        output =
            textarea {
                promptText = "结果"
                isWrapText = true
            }
    }

    private fun doCrypto() {
        runAsync {
            isProcessing.value = true
            if (isEncrypt)
                if (isFile.get())
                    inputText.split("\n|\r\n".toRegex()).joinToString("\n") {
                        controller.encryptByFile(keyByteArray, it, ivByteArray, cipher)
                    }
                else
                    controller.encrypt(
                        keyByteArray,
                        inputText,
                        ivByteArray,
                        cipher,
                        selectedCharset.get()
                    )
            else if (isFile.get())
                inputText.split("\n|\r\n".toRegex()).joinToString("\n") {
                    controller.decryptByFile(keyByteArray, it, ivByteArray, cipher)
                }
            else
                controller.decrypt(
                    keyByteArray,
                    inputText,
                    ivByteArray,
                    cipher,
                    selectedCharset.get()
                )
        } ui
            {
                isProcessing.value = false
                output.text = it
            }
    }
}
