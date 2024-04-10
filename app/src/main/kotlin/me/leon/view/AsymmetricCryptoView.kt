package me.leon.view

import javafx.beans.property.*
import javafx.scene.control.*
import javafx.scene.layout.Priority
import me.leon.*
import me.leon.config.TEXT_AREA_LINES
import me.leon.controller.AsymmetricCryptoController
import me.leon.domain.SimpleMsgEvent
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.*
import me.leon.ext.fx.*
import tornadofx.*

class AsymmetricCryptoView : Fragment(FX.messages["asymmetric"]) {
    private val controller: AsymmetricCryptoController by inject()

    private var timeConsumption = 0L
    private var startTime = 0L
    private var isEncrypt = true
    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private val algos = ASYMMETRIC_ALGOS.keys.toMutableList()

    override val closeable = SimpleBooleanProperty(false)
    private val singleLine = SimpleBooleanProperty(false)
    private val privateKeyEncrypt = SimpleBooleanProperty(false)
    private val processing = SimpleBooleanProperty(false)
    private val enablePadding = SimpleBooleanProperty(true)
    private val showDerivedKey = SimpleBooleanProperty(true)
    private val selectedAlg = SimpleStringProperty(algos.first())
    private val selectedBits = SimpleIntegerProperty(ASYMMETRIC_ALGOS[selectedAlg.get()]!!.first())

    private var cbBits: ComboBox<Number> by singleAssign()
    private var taInput: TextArea by singleAssign()
    private var taPubKey: TextArea by singleAssign()
    private var taPriKey: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var tgInput: ToggleGroup by singleAssign()
    private var tgOutput: ToggleGroup by singleAssign()

    private val isPrivate
        get() =
            privateKeyEncrypt.get() && isEncrypt || privateKeyEncrypt.get().not() && isEncrypt.not()

    private val isPublic
        get() = privateKeyEncrypt.get() && !isEncrypt || privateKeyEncrypt.get().not() && isEncrypt

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

    @Suppress("TrimMultilineRawString")
    private val info
        get() =
            "${selectedAlg.get()}  bits: ${selectedBits.get()}  mode: ${
                if (privateKeyEncrypt.get()) {
                    "private key encrypt"
                } else {
                    "public key encrypt"
                }
            }  " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "cost: $timeConsumption ms"

    private val selectedPadding = SimpleStringProperty(RSA_PADDINGS.first())
    private var labelInfo: Label by singleAssign()

    private val alg
        get() =
            with(selectedAlg.get()) {
                if (this == "RSA") "$this/NONE/${selectedPadding.get()}" else this
            }

    private val isPrivateKey
        get() = isEncrypt && privateKeyEncrypt.get() || !isEncrypt && !privateKeyEncrypt.get()

    private val keyEventHandler = fileDraggedHandler {
        val firstFile = it.first()
        taPubKey.text =
            if (firstFile.extension in listOf("pk8", "key", "der")) {
                val text = firstFile.readText()
                if (text.startsWith("-----BEGIN")) {
                    text
                } else {
                    firstFile.readBytes().base64()
                }
            } else if (firstFile.extension in listOf("cer", "crt")) {
                firstFile.parsePublicKeyFromCerFile()
            } else {
                firstFile.properText()
            }
        updateKeySize()
    }

    private val priKeyEventHandler = fileDraggedHandler {
        val firstFile = it.first()
        taPriKey.text =
            if (firstFile.extension in listOf("pk8", "key", "der")) {
                val text = firstFile.readText()
                if (text.startsWith("-----BEGIN")) {
                    text
                } else {
                    firstFile.readBytes().base64()
                }
            } else if (firstFile.extension in listOf("cer", "crt")) {
                firstFile.parsePublicKeyFromCerFile()
            } else {
                firstFile.properText()
            }
        updateKeySize()
    }

    private val inputEventHandler = fileDraggedHandler { taInput.text = it.first().properText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            addClass(Styles.left)
            label(messages["input"]) { tooltip("加密时为明文,解密时为base64编码的密文") }
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
                action { inputText = clipboardText() }
            }
        }
        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            prefHeight = DEFAULT_SPACING_16X
            onDragEntered = inputEventHandler
            prefRowCount = TEXT_AREA_LINES
        }

        hbox {
            label(messages["key"])
            button(graphic = imageview("/img/jump.png")) {
                tooltip(messages["goSignature"])
                action {
                    fire(SimpleMsgEvent(taPubKey.text, 1))
                    fire(SimpleMsgEvent(taPriKey.text, 2))
                    val tabPane = findParentOfType(TabPane::class)
                    tabPane
                        ?.selectionModel
                        ?.select(tabPane.tabs.first { it.text == messages["signVerify"] })
                }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING_3X
            taPubKey = textarea {
                promptText = messages["inputHintAsyPub"]
                isWrapText = true
                onDragEntered = keyEventHandler
            }
            taPriKey = textarea {
                promptText = messages["inputHintAsyPri"]
                isWrapText = true
                onDragEntered = priKeyEventHandler
            }
        }

        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, algos) { cellFormat { text = it.toString() } }
            selectedAlg.addListener { _, _, newValue ->
                newValue?.run {
                    cbBits.items = ASYMMETRIC_ALGOS[newValue]!!.asObservable()
                    selectedBits.set(ASYMMETRIC_ALGOS[newValue]!!.first())
                    cbBits.isDisable = ASYMMETRIC_ALGOS[newValue]?.size == 1
                    enablePadding.value = newValue == "RSA"
                    showDerivedKey.value = newValue == "RSA"
                }
            }
            label(messages["bits"])
            cbBits =
                combobox(selectedBits, ASYMMETRIC_ALGOS[selectedAlg.get()]) {
                    cellFormat { text = it.toString() }
                }
            label("padding:")
            combobox(selectedPadding, RSA_PADDINGS) {
                enableWhen(enablePadding)
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
            checkbox(messages["singleLine"], singleLine)
            checkbox(messages["priEncrypt"], privateKeyEncrypt) {
                tooltip("默认公钥加密，私钥解密。开启后私钥加密，公钥解密")
            }

            button(messages["run"], imageview(IMG_RUN)) { action { doCrypto() } }
            button(messages["genKeypair"]) {
                enableWhen(!processing)
                action {
                    processing.value = true
                    runAsync { genBase64KeyArray(alg, listOf(selectedBits.value.toInt())) } ui
                        {
                            processing.value = false
                            taPubKey.text = it[0]
                            taPriKey.text = it[1]
                        }
                }
            }
            button(messages["deriveKey"]) {
                visibleWhen(showDerivedKey)
                enableWhen(!processing)
                action {
                    processing.value = true
                    runAsync { catch({ it }) { taPriKey.text.privateKeyDerivedPublicKey(alg) } } ui
                        {
                            processing.value = false
                            taPubKey.text = it
                        }
                }
            }
            button("RSA info") {
                enableWhen(!processing)
                action {
                    processing.value = true
                    runAsync {
                        catch({ it }) {
                            println("________")
                            controller.parseInfo(taInput.text, alg)
                        }
                    } ui
                        {
                            processing.value = false
                            taOutput.text = it
                        }
                }
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
                    inputText = outputText
                    outputText = ""
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

    private fun updateKeySize() {
        runAsync {
            runCatching {
                    if (isPrivateKey) {
                        controller.lengthFromPri(taPriKey.text.trim())
                    } else {
                        controller.lengthFromPub(taPubKey.text.trim())
                    }
                }
                .getOrDefault(1024)
        } ui { selectedBits.set(it) }
    }

    private fun doCrypto() {
        val isKeyEmpty = isPrivate && taPriKey.text.isEmpty() || isPublic && taPubKey.text.isEmpty()
        if (isKeyEmpty || inputText.isEmpty()) {
            outputText = "请输入key 或者 待处理内容"
            return
        }

        runAsync {
            processing.value = true
            startTime = System.currentTimeMillis()
            runCatching {
                    if (isEncrypt) {
                        if (isPrivateKey) {
                            controller.priEncrypt(
                                taPriKey.text,
                                alg,
                                inputText,
                                singleLine.get(),
                                inputEncode = inputEncode,
                                outputEncode = outputEncode
                            )
                        } else {
                            controller.pubEncrypt(
                                taPubKey.text,
                                alg,
                                inputText,
                                singleLine.get(),
                                inputEncode = inputEncode,
                                outputEncode = outputEncode
                            )
                        }
                    } else if (privateKeyEncrypt.get()) {
                        controller.pubDecrypt(
                            taPubKey.text,
                            alg,
                            inputText,
                            singleLine.get(),
                            inputEncode,
                            outputEncode
                        )
                    } else {
                        controller.priDecrypt(
                            taPriKey.text,
                            alg,
                            inputText,
                            singleLine.get(),
                            inputEncode,
                            outputEncode
                        )
                    }
                }
                .getOrElse { it.stacktrace() }
        } ui
            {
                processing.value = false
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }
}
