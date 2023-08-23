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

class SymmetricCryptoView : Fragment(messages["symmetricBlock"]) {
    private val controller: SymmetricCryptoController by inject()

    private var isEncrypt = true
    private var timeConsumption = 0L
    private var startTime = 0L
    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private val customAlg = arrayOf("XXTEA", "XOR")
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
            "Tnepres",
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
            "ISO7816-4Padding",
            "ISO10126-2Padding"
        )
    private val modes = mutableListOf("CBC", "ECB", "CFB", "OFB", "CTR", "GCM", "CCM", "EAX", "OCB")

    override val closeable = SimpleBooleanProperty(false)
    private val fileProperty = SimpleBooleanProperty(false)
    private val processing = SimpleBooleanProperty(false)
    private val singleLine = SimpleBooleanProperty(false)
    private val enableIv = SimpleBooleanProperty(true)
    private val autoKeyIv = SimpleBooleanProperty(false)
    private val enableAEAD = SimpleBooleanProperty(false)
    private val enableModAndPadding = SimpleBooleanProperty(true)
    private val selectedAlg = SimpleStringProperty(algs[2])
    private val selectedPadding = SimpleStringProperty(paddingsAlg.first())
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())
    private val selectedMod = SimpleStringProperty(modes.first())

    private var taInput: TextArea by singleAssign()
    private var tgInput: ToggleGroup by singleAssign()
    private var tgOutput: ToggleGroup by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var labelInfo: Label by singleAssign()
    private val keyIvInputView = KeyIvInputView(enableIv, enableAEAD, autoKeyIv)

    private val inputText: String
        get() = taInput.text

    private val outputText: String
        get() = taOutput.text

    private val info
        get() =
            "Cipher: $cipher " +
                "Key/Iv: ${keyIvInputView.keyByteArray.size}/${keyIvInputView.ivByteArray.size} B  " +
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
        get() =
            with(selectedAlg.get()) {
                if (this in customAlg) {
                    selectedAlg.get()
                } else {
                    "$this/${selectedMod.get()}/${selectedPadding.get()}"
                }
            }

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
            contextmenu {
                item(messages["loadFromNetLoop"]) {
                    action { runAsync { inputText.simpleReadFromNet() } ui { taInput.text = it } }
                }
            }
        }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, algs) { cellFormat { text = it } }
            label("mode:")
            combobox(selectedMod, modes) {
                enableWhen(enableModAndPadding)
                cellFormat { text = it }
            }
            label("padding:")
            combobox(selectedPadding, paddingsAlg) {
                enableWhen(enableModAndPadding)
                cellFormat { text = it }
            }
            label("charset:")
            combobox(selectedCharset, CHARSETS) { cellFormat { text = it } }
        }
        add(keyIvInputView)
        selectedAlg.addListener { _, _, newValue ->
            newValue?.run {
                enableModAndPadding.value = newValue !in customAlg
                if (newValue in customAlg) {
                    enableIv.value = false
                }
                println("alg $newValue")
            }
        }
        selectedMod.addListener { _, _, newValue ->
            newValue?.run {
                enableIv.value = newValue != "ECB"
                enableAEAD.value = newValue.contains(AEAD_MODE_REG)
                if (enableAEAD.value) {
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
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doCrypto() {
        runAsync {
            startTime = System.currentTimeMillis()
            processing.value = true
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
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
