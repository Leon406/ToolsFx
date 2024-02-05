package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.*
import javafx.scene.layout.Priority
import me.leon.*
import me.leon.controller.DigestController
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class DigestView : Fragment(messages["hash"]) {
    private val controller: DigestController by inject()

    private var timeConsumption = 0L
    private var startTime = 0L
    private var inputEncode = "raw"
    private var method = "MD5"

    override val closeable = SimpleBooleanProperty(false)
    private val fileMode = SimpleBooleanProperty(false)
    private val processing = SimpleBooleanProperty(false)
    private val singleLine = SimpleBooleanProperty(false)
    private val maskMode = SimpleBooleanProperty(false)
    private val enableFileMode = SimpleBooleanProperty(true)
    private val selectedAlg = SimpleStringProperty(ALGOS_HASH.keys.first())
    private val selectedBits = SimpleStringProperty(ALGOS_HASH.values.first().first())

    private var taInput: TextArea by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var tfCount: TextField by singleAssign()
    private var tfMask: TextField by singleAssign()
    private var tfCustomDict: TextField by singleAssign()
    private var cbBits: ComboBox<String> by singleAssign()

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

    private val times
        get() = tfCount.text.toIntOrNull() ?: 1.also { tfCount.text = "1" }

    private val eventHandler = fileDraggedHandler {
        inputText =
            if (fileMode.get()) {
                it.joinToString(System.lineSeparator(), transform = File::getAbsolutePath)
            } else {
                it.first().properText()
            }
    }

    private val info
        get() =
            "Hash: $method bits: ${selectedBits.get()} " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "count: $times cost: $timeConsumption ms  " +
                "file mode: ${fileMode.get()}"

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            addClass(Styles.left)
            label(messages["input"])

            togglegroup {
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
            onDragEntered = eventHandler
        }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlg, ALGOS_HASH.keys.toMutableList()) { cellFormat { text = it } }
            label(messages["bits"])
            cbBits =
                combobox(selectedBits, ALGOS_HASH.values.first()) {
                    cellFormat { text = it }
                    isDisable = true
                }
        }

        selectedAlg.addListener { _, _, newValue ->
            newValue?.run {
                cbBits.items = ALGOS_HASH[newValue]!!.asObservable()
                selectedBits.set(ALGOS_HASH[newValue]!!.first())
                cbBits.isDisable = ALGOS_HASH[newValue]!!.size == 1
            }
        }

        selectedBits.addListener { _, _, new ->
            println("selectedBits __ $new")
            new?.run {
                method =
                    if (selectedAlg.get() in arrayOf("PasswordHashing", "Windows")) {
                        enableFileMode.value = false
                        new
                    } else {
                        enableFileMode.value = true
                        "${selectedAlg.get()}${new.takeIf { ALGOS_HASH[selectedAlg.get()]!!.size > 1 }.orEmpty()}"
                            .replace("SHA2", "SHA-")
                            .replace(
                                "(Haraka|GOST3411-2012|Keccak|SHA3|Blake2b|Blake2s|DSTU7564|Skein)"
                                    .toRegex(),
                                "$1-"
                            )
                    }
                println("算法 $method")
                if (inputText.isNotEmpty() && !fileMode.get()) {
                    doHash()
                }
            }
        }
        hbox {
            addClass(Styles.left)
            paddingLeft = DEFAULT_SPACING
            checkbox(messages["fileMode"], fileMode) { enableWhen(enableFileMode) }
            checkbox(messages["singleLine"], singleLine)

            label("times:")
            tfCount =
                textfield("1") {
                    textFormatter = intTextFormatter
                    prefWidth = DEFAULT_SPACING_8X
                    enableWhen(!fileMode)
                }
            button(messages["run"], imageview(IMG_RUN)) {
                enableWhen(!processing)
                action { doHash() }
            }
            button("crack", imageview(IMG_CRACK)) {
                enableWhen(!processing)
                action { crack() }
                tooltip("default top1000 password, you can add your dict file at /dict") {
                    isWrapText = true
                }
            }
            button("cmd5", imageview("/img/browser.png")) {
                action { "https://www.cmd5.com/".openInBrowser() }
            }
            checkbox("mask", maskMode)
        }
        hbox {
            visibleWhen(maskMode)
            addClass(Styles.left)
            paddingLeft = DEFAULT_SPACING
            label("mask:")
            tfMask =
                textfield("?d?l?u?c??") {
                    prefWidth = DEFAULT_SPACING_32X
                    tooltip(
                        "like hash-cat,?d ?l ?u ?a ?s, and more: ?? for ?, ?* for custom dict,?c for letter"
                    )
                }

            label("custom dict:")
            tfCustomDict =
                textfield("0123456789") {
                    prefWidth = DEFAULT_SPACING_32X
                    tooltip("for mask ?*")
                }
        }
        hbox {
            label(messages["output"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { outputText.copy() }
            }
        }
        taOutput = textarea {
            vgrow = Priority.ALWAYS
            promptText = messages["outputHint"]
            isWrapText = true
            contextmenu {
                item("uppercase") { action { taOutput.text = taOutput.text.uppercase() } }
                item("lowercase") { action { taOutput.text = taOutput.text.lowercase() } }
                item("base64") { action { taOutput.text = taOutput.text.hex2ByteArray().base64() } }
                item("hex") { action { taOutput.text = taOutput.text.base64Decode().toHex() } }
            }
        }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun crack() {
        runAsync {
            processing.value = true
            startTime = System.currentTimeMillis()
            if (maskMode.get()) {
                controller.maskCrack(
                    method,
                    inputText
                        .decodeToByteArray(inputEncode.takeUnless { it == "raw" } ?: "hex")
                        .encodeTo("hex"),
                    tfMask.text,
                    tfCustomDict.text
                )
            } else {
                if (method.startsWith("SpringSecurity")) {
                    controller.passwordHashingCrack(method, inputText)
                } else {
                    controller.crack(
                        method,
                        if (method == "mysql5") {
                            inputText
                        } else {
                            inputText
                                .decodeToByteArray(inputEncode.takeUnless { it == "raw" } ?: "hex")
                                .encodeTo("hex")
                        }
                    )
                }
            }
        } ui
            {
                processing.value = false
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy) {
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
                }
                System.gc()
            }
    }

    private fun doHash() =
        runAsync {
            processing.value = true
            startTime = System.currentTimeMillis()
            if (fileMode.get()) {
                inputText.lineAction2String { controller.digestFile(method, it) }
            } else {
                var result: String = inputText
                repeat(times) { time ->
                    result =
                        controller.digest(
                            method,
                            result,
                            "raw".takeIf { time > 0 } ?: inputEncode,
                            singleLine.get()
                        )
                }
                result
            }
        } ui
            {
                processing.value = false
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy) {
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
                }
            }
}
