package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.*
import me.leon.ALGOS_HASH
import me.leon.Styles
import me.leon.controller.DigestController
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class DigestView : Fragment(messages["hash"]) {
    private val controller: DigestController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isFileMode = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private val isEnableFileMode = SimpleBooleanProperty(true)
    private lateinit var taInput: TextArea
    private lateinit var labelInfo: Label
    lateinit var taOutput: TextArea
    private lateinit var tfCount: TextField
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

    var method = "MD5"

    private val eventHandler = fileDraggedHandler {
        inputText =
            if (isFileMode.get())
                it.joinToString(System.lineSeparator(), transform = File::getAbsolutePath)
            else
                with(it.first()) {
                    if (length() <= 10 * 1024 * 1024)
                        if (realExtension() in unsupportedExts) "unsupported file extension"
                        else readText()
                    else "not support file larger than 10M"
                }
    }

    private val selectedAlgItem = SimpleStringProperty(ALGOS_HASH.keys.first())
    private val selectedBits = SimpleStringProperty(ALGOS_HASH.values.first().first())
    lateinit var cbBits: ComboBox<String>
    private val info
        get() =
            "Hash: $method bits: ${selectedBits.get()} " +
                "${messages["inputLength"]}: ${inputText.length}  " +
                "${messages["outputLength"]}: ${outputText.length}  " +
                "count: $times cost: $timeConsumption ms  " +
                "file mode: ${isFileMode.get()}"

    private var timeConsumption = 0L
    private var startTime = 0L
    private var inputEncode = "raw"

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

            button(graphic = imageview("/img/import.png")) {
                action { inputText = clipboardText() }
            }
        }
        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            addClass(Styles.left)
            label(messages["alg"])
            combobox(selectedAlgItem, ALGOS_HASH.keys.toMutableList()) { cellFormat { text = it } }
            label(messages["bits"])
            cbBits =
                combobox(selectedBits, ALGOS_HASH.values.first()) {
                    cellFormat { text = it }
                    isDisable = true
                }
        }

        selectedAlgItem.addListener { _, _, newValue ->
            newValue?.run {
                cbBits.items = ALGOS_HASH[newValue]!!.asObservable()
                selectedBits.set(ALGOS_HASH[newValue]!!.first())
                cbBits.isDisable = ALGOS_HASH[newValue]!!.size == 1
            }
        }

        selectedBits.addListener { _, _, newValue ->
            println("selectedBits __ $newValue")
            newValue?.run {
                method =
                    if (selectedAlgItem.get() == "PasswordHashing") {
                        isEnableFileMode.value = false
                        newValue
                    } else {
                        isEnableFileMode.value = true
                        "${selectedAlgItem.get()}${
                            newValue
                                .takeIf { ALGOS_HASH[selectedAlgItem.get()]!!.size > 1 } ?: ""
                        }"
                            .replace("SHA2", "SHA-")
                            .replace(
                                "(Haraka|GOST3411-2012|Keccak|SHA3|Blake2b|Blake2s|DSTU7564|Skein)".toRegex(),
                                "$1-"
                            )
                    }
                println("算法 $method")
                if (inputText.isNotEmpty() && !isFileMode.get()) {
                    doHash()
                }
            }
        }
        hbox {
            addClass(Styles.left)
            paddingLeft = DEFAULT_SPACING
            checkbox(messages["fileMode"], isFileMode) { enableWhen(isEnableFileMode) }
            checkbox(messages["singleLine"], isSingleLine)

            label("times:")
            tfCount =
                textfield("1") {
                    textFormatter = intTextFormatter
                    prefWidth = DEFAULT_SPACING_8X
                    enableWhen(!isFileMode)
                }
            button(messages["run"], imageview("/img/run.png")) {
                enableWhen(!isProcessing)
                action { doHash() }
            }
            button("crack", imageview("/img/crack.png")) {
                enableWhen(!isProcessing)
                action { crack() }
                tooltip("default top1000 password, you can add your dict file at /dict") {
                    isWrapText = true
                }
            }
            button("cmd5", imageview("/img/browser.png")) {
                action { "https://www.cmd5.com/".openInBrowser() }
            }
        }
        hbox {
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
        }
        taOutput =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
                contextmenu {
                    item("uppercase") { action { taOutput.text = taOutput.text.uppercase() } }
                    item("lowercase") { action { taOutput.text = taOutput.text.lowercase() } }
                    item("base64") {
                        action { taOutput.text = taOutput.text.hex2ByteArray().base64() }
                    }
                    item("hex") { action { taOutput.text = taOutput.text.base64Decode().toHex() } }
                }
            }
    }

    private fun crack() {
        runAsync {
            isProcessing.value = true
            startTime = System.currentTimeMillis()
            val target =
                inputText
                    .decodeToByteArray(inputEncode.takeUnless { it == "raw" } ?: "hex")
                    .encodeTo("hex")
            controller.crack(method, target)
        } ui
            {
                isProcessing.value = false
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy)
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
            }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doHash() =
        runAsync {
            isProcessing.value = true
            startTime = System.currentTimeMillis()
            if (isFileMode.get()) inputText.lineAction2String { controller.digestFile(method, it) }
            else {
                var result: String = inputText
                repeat(times) {
                    result = controller.digest(method, result, inputEncode, isSingleLine.get())
                }
                result
            }
        } ui
            {
                isProcessing.value = false
                outputText = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                if (Prefs.autoCopy)
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
            }
}
