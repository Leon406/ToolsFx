package me.leon.view

import javafx.beans.property.*
import javafx.scene.control.*
import me.leon.Styles
import me.leon.controller.AsymmetricCryptoController
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.*
import me.leon.ext.fx.*
import tornadofx.*

class AsymmetricCryptoView : Fragment(FX.messages["asymmetric"]) {
    private val controller: AsymmetricCryptoController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private val privateKeyEncrypt = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private val isEnablePadding = SimpleBooleanProperty(true)
    private val isShowDerivedKey = SimpleBooleanProperty(true)

    private var cbBits: ComboBox<Number> by singleAssign()
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
    private var timeConsumption = 0L
    private var startTime = 0L
    private val info
        get() =
            "${selectedAlg.get()}  bits: ${selectedBits.get()}  mode: ${
                if (privateKeyEncrypt.get()) "private key encrypt"
                else "public key encrypt"
            }  " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "cost: $timeConsumption ms"
    private val selectedPadding = SimpleStringProperty(RSA_PADDINGS.first())
    private lateinit var labelInfo: Label
    private var keyText: String
        get() = taKey.text.trim()
        set(value) {
            taKey.text = value
        }

    private val alg
        get() =
            with(selectedAlg.get()) {
                if (this == "RSA") "$this/NONE/${selectedPadding.get()}" else this
            }
    private var isEncrypt = true
    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup

    private val algos = ASYMMETRIC_ALGOS.keys.toMutableList()
    private val selectedAlg = SimpleStringProperty(algos.first())
    private val selectedBits = SimpleIntegerProperty(ASYMMETRIC_ALGOS[selectedAlg.get()]!!.first())
    private val isPrivateKey
        get() = isEncrypt && privateKeyEncrypt.get() || !isEncrypt && !privateKeyEncrypt.get()

    private val keyEventHandler = fileDraggedHandler {
        val firstFile = it.first()
        keyText =
            if (firstFile.extension in listOf("pk8", "key", "der")) firstFile.readBytes().base64()
            else if (firstFile.extension in listOf("cer", "crt"))
                firstFile.parsePublicKeyFromCerFile()
            else
                with(firstFile) {
                    if (length() <= 128 * 1024)
                        if (realExtension() in unsupportedExts) "unsupported file extension"
                        else readText()
                    else "not support file larger than 128KB"
                }
        updateKeySize()
    }

    private fun updateKeySize() {
        runAsync {
            runCatching {
                    if (isPrivateKey) {
                        controller.lengthFromPri(keyText)
                    } else {
                        controller.lengthFromPub(keyText)
                    }
                }
                .getOrDefault(1024)
        } ui { selectedBits.set(it) }
    }

    private val inputEventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 128 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 128KB"
            }
    }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            addClass(Styles.left)
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
            button(graphic = imageview("/img/import.png")) {
                action {
                    keyText = clipboardText()
                    updateKeySize()
                }
            }
        }
        taKey =
            textarea {
                promptText = messages["inputHintAsy"]
                isWrapText = true
                onDragEntered = keyEventHandler
            }

        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, algos) { cellFormat { text = it.toString() } }
            selectedAlg.addListener { _, _, newValue ->
                newValue?.run {
                    cbBits.items = ASYMMETRIC_ALGOS[newValue]!!.asObservable()
                    selectedBits.set(ASYMMETRIC_ALGOS[newValue]!!.first())
                    cbBits.isDisable = ASYMMETRIC_ALGOS[newValue]!!.size == 1
                    isEnablePadding.value = newValue == "RSA"
                    isShowDerivedKey.value = newValue == "RSA"
                }
            }
            label(messages["bits"])
            cbBits =
                combobox(selectedBits, ASYMMETRIC_ALGOS[selectedAlg.get()]) {
                    cellFormat { text = it.toString() }
                }
            label("padding:")
            combobox(selectedPadding, RSA_PADDINGS) {
                enableWhen(isEnablePadding)
                cellFormat { text = it }
            }
        }
        hbox {
            addClass(Styles.center)
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
                enableWhen(!isProcessing)
                action {
                    isProcessing.value = true
                    runAsync { genBase64KeyArray(alg, listOf(selectedBits.value.toInt())) } ui
                        {
                            isProcessing.value = false
                            if (isPrivateKey) {
                                taInput.text = it[0]
                                taKey.text = it[1]
                            } else {
                                taInput.text = it[1]
                                taKey.text = it[0]
                            }
                        }
                }
            }
            button(messages["deriveKey"]) {
                visibleWhen(isShowDerivedKey)
                enableWhen(!isProcessing)
                action {
                    isProcessing.value = true
                    runAsync { catch({ it }) { taKey.text.privateKeyDerivedPublicKey(alg) } } ui
                        {
                            isProcessing.value = false
                            taOutput.text = it
                        }
                }
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
            isProcessing.value = true
            startTime = System.currentTimeMillis()
            if (isEncrypt)
                if (privateKeyEncrypt.get())
                    controller.priEncrypt(
                        keyText,
                        alg,
                        inputText,
                        isSingleLine.get(),
                        inputEncode = inputEncode,
                        outputEncode = outputEncode
                    )
                else
                    controller.pubEncrypt(
                        keyText,
                        alg,
                        inputText,
                        isSingleLine.get(),
                        inputEncode = inputEncode,
                        outputEncode = outputEncode
                    )
            else if (privateKeyEncrypt.get())
                controller.pubDecrypt(
                    keyText,
                    alg,
                    inputText,
                    isSingleLine.get(),
                    inputEncode,
                    outputEncode
                )
            else
                controller.priDecrypt(
                    keyText,
                    alg,
                    inputText,
                    isSingleLine.get(),
                    inputEncode,
                    outputEncode
                )
        } ui
            {
                isProcessing.value = false
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
