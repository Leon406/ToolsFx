package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import me.leon.controller.DigestController
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.Prefs
import me.leon.ext.clipboardText
import me.leon.ext.copy
import me.leon.ext.fileDraggedHandler
import me.leon.ext.showToast
import tornadofx.FX.Companion.messages
import tornadofx.View
import tornadofx.action
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.button
import tornadofx.checkbox
import tornadofx.combobox
import tornadofx.enableWhen
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingLeft
import tornadofx.textarea
import tornadofx.vbox

class DigestView : View(messages["hash"]) {
    private val controller: DigestController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isFileMode = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var labelInfo: Label
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
    var method = "MD5"

    private val eventHandler = fileDraggedHandler {
        inputText =
            if (isFileMode.get())
                it.joinToString(System.lineSeparator(), transform = File::getAbsolutePath)
            else it.first().readText()
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
            "CRC" to listOf("32"),
        )
    private val selectedAlgItem = SimpleStringProperty(algs.keys.first())
    private val selectedBits = SimpleStringProperty(algs.values.first().first())
    lateinit var cbBits: ComboBox<String>
    private val info
        get() = "Hash: $method bits: ${selectedBits.get()}  file mode: ${isFileMode.get()}"

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
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
                    "${selectedAlgItem.get()}${newValue.takeIf { algs[selectedAlgItem.get()]!!.size > 1 } ?: ""}"
                        .replace("SHA2", "SHA-")
                        .replace(
                            "(Haraka|GOST3411-2012|Keccak|SHA3|Blake2b|Blake2s|DSTU7564|Skein)".toRegex(),
                            "$1-"
                        )
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
            checkbox(messages["fileMode"], isFileMode)
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
            if (isFileMode.get())
                inputText.split("\n|\r\n".toRegex()).joinToString("\n") {
                    controller.digestFile(method, it)
                }
            else controller.digest(method, inputText)
        } ui
            {
                isProcessing.value = false
                outputText = it
                labelInfo.text = info
                if (Prefs.autoCopy)
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
            }
}
