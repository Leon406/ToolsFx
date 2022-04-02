package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.controller.DigestController
import me.leon.ext.*
import me.leon.ext.crypto.passwordHashingTypes
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

    // https://www.bouncycastle.org/specifications.html
    private val algs =
        linkedMapOf(
            "MD5" to listOf("128"),
            "MD4" to listOf("128"),
            "MD2" to listOf("128"),
            "SM3" to listOf("256"),
            "Tiger" to listOf("192"),
            "Whirlpool" to listOf("512"),
            "SHA1" to listOf("160"),
            "SHA2" to listOf("224", "256", "384", "512", "512/224", "512/256"),
            "SHA3" to listOf("224", "256", "384", "512"),
            "RIPEMD" to listOf("128", "160", "256", "320"),
            "Keccak" to listOf("224", "256", "288", "384", "512"),
            "Blake2b" to listOf("160", "256", "384", "512"),
            "Blake2s" to listOf("160", "224", "256"),
            "DSTU7564" to listOf("256", "384", "512"),
            "Skein" to
                listOf(
                    "256-160",
                    "256-224",
                    "256-256",
                    "512-128",
                    "512-160",
                    "512-224",
                    "512-256",
                    "512-384",
                    "512-512",
                    "1024-384",
                    "1024-512",
                    "1024-1024"
                ),
            "GOST3411" to listOf("256"),
            "GOST3411-2012" to listOf("256", "512"),
            "Haraka" to listOf("256", "512"),
            "CRC" to listOf("32", "64"),
            "PasswordHashing" to passwordHashingTypes,
        )
    private val selectedAlgItem = SimpleStringProperty(algs.keys.first())
    private val selectedBits = SimpleStringProperty(algs.values.first().first())
    lateinit var cbBits: ComboBox<String>
    private val info
        get() =
            "Hash: $method bits: ${selectedBits.get()} count: $times cost: $timeConsumption ms" +
                "  file mode: ${isFileMode.get()}"

    private var timeConsumption = 0L
    private var startTime = 0L

    private var inputEncode = "raw"
    private lateinit var tgInput: ToggleGroup

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["input"])
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
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            label(messages["alg"])
            combobox(selectedAlgItem, algs.keys.toMutableList()) { cellFormat { text = it } }
            label(messages["bits"])
            cbBits =
                combobox(selectedBits, algs.values.first()) {
                    cellFormat { text = it }
                    isDisable = true
                }
        }

        selectedAlgItem.addListener { _, _, newValue ->
            newValue?.run {
                cbBits.items = algs[newValue]!!.asObservable()
                selectedBits.set(algs[newValue]!!.first())
                cbBits.isDisable = algs[newValue]!!.size == 1
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
                        "${selectedAlgItem.get()}${newValue.takeIf { algs[selectedAlgItem.get()]!!.size > 1 } ?: ""}"
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
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            paddingLeft = DEFAULT_SPACING
            checkbox(messages["fileMode"], isFileMode) { enableWhen(isEnableFileMode) }
            checkbox(messages["singleLine"], isSingleLine)

            label("times:")
            tfCount =
                textfield("1") {
                    prefWidth = DEFAULT_SPACING_8X
                    enableWhen(!isFileMode)
                }
            button(messages["run"], imageview("/img/run.png")) {
                enableWhen(!isProcessing)
                action { doHash() }
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
