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
import me.leon.ext.crypto.AEAD_MODE_REG
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class SymmetricCryptoView : Fragment(messages["symmetricBlock"]) {
    private val controller: SymmetricCryptoController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isFile = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private val isEnableIv = SimpleBooleanProperty(true)
    private val isEnableAEAD = SimpleBooleanProperty(false)
    private val isEnableModAndPadding = SimpleBooleanProperty(true)
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
            "Cipher: $cipher   charset: ${selectedCharset.get()}  file mode:  ${isFile.get()} " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "cost: $timeConsumption ms"
    private lateinit var labelInfo: Label
    private val keyIvInputView = KeyIvInputView(isEnableIv, isEnableAEAD)
    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private val customAlg = arrayOf("XXTEA", "XOR")

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            if (isFile.get())
                it.joinToString(System.lineSeparator(), transform = File::getAbsolutePath)
            else
                with(it.first()) {
                    if (length() <= 128 * 1024)
                        if (realExtension() in unsupportedExts) "unsupported file extension"
                        else readText()
                    else "not support file larger than 128K,plz use file mode!!!"
                }
    }
    private val algs =
        mutableListOf(
            "DES",
            "DESede",
            "AES",
            "Rijndael",
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
            "Shacal2",
            "GOST28147",
            "GOST3412-2015",
            "Noekeon",
            "IDEA",
            "SEED",
            "TEA",
            "XTEA",
            // coco2d encrypt
            "XXTEA",
            "XOR",
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
    private val selectedPadding = SimpleStringProperty(paddingsAlg.first())
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())
    private val selectedMod = SimpleStringProperty(modes.first())

    private val cipher
        get() =
            with(selectedAlg.get()) {
                if (this in customAlg) selectedAlg.get()
                else "$this/${selectedMod.get()}/${selectedPadding.get()}"
            }

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
                contextmenu {
                    item(messages["loadFromNetLoop"]) {
                        action {
                            runAsync { inputText.simpleReadFromNet() } ui { taInput.text = it }
                        }
                    }
                }
            }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, algs) { cellFormat { text = it } }
            label("mode:")
            combobox(selectedMod, modes) {
                enableWhen(isEnableModAndPadding)
                cellFormat { text = it }
            }
            label("padding:")
            combobox(selectedPadding, paddingsAlg) {
                enableWhen(isEnableModAndPadding)
                cellFormat { text = it }
            }
            label("charset:")
            combobox(selectedCharset, CHARSETS) { cellFormat { text = it } }
        }
        add(keyIvInputView)
        selectedAlg.addListener { _, _, newValue ->
            newValue?.run {
                isEnableModAndPadding.value = newValue !in customAlg
                if (newValue in customAlg) {
                    isEnableIv.value = false
                }
                println("alg $newValue")
            }
        }
        selectedMod.addListener { _, _, newValue ->
            newValue?.run {
                isEnableIv.value = newValue != "ECB"
                isEnableAEAD.value = newValue.contains(AEAD_MODE_REG)
                if (isEnableAEAD.value) {
                    selectedPadding.value = "NoPadding"
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
                    tgOutput.selectToggle(tgOutput.toggles[if (isEncrypt) 1 else 0])
                    if (isEncrypt) tgInput.selectToggle(tgInput.toggles[0])
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
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doCrypto() {
        runAsync {
            startTime = System.currentTimeMillis()
            isProcessing.value = true
            if (isEncrypt)
                if (isFile.get())
                    inputText.lineAction2String {
                        controller.encryptByFile(
                            keyIvInputView.keyByteArray,
                            it,
                            keyIvInputView.ivByteArray,
                            cipher,
                            keyIvInputView.associatedData
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
                        outputEncode,
                        keyIvInputView.associatedData
                    )
            else if (isFile.get())
                inputText.lineAction2String {
                    controller.decryptByFile(
                        keyIvInputView.keyByteArray,
                        it,
                        keyIvInputView.ivByteArray,
                        cipher,
                        keyIvInputView.associatedData
                    )
                }
            else {
                controller.decrypt(
                    keyIvInputView.keyByteArray,
                    inputText,
                    keyIvInputView.ivByteArray,
                    cipher,
                    selectedCharset.get(),
                    isSingleLine.get(),
                    inputEncode,
                    outputEncode,
                    keyIvInputView.associatedData
                )
            }
        } ui
            {
                isProcessing.value = false
                taOutput.text = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
