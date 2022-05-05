package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.*
import kotlin.collections.set
import kotlin.system.measureTimeMillis
import me.leon.SimpleMsgEvent
import me.leon.Styles
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.EncodeType
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class StringProcessView : Fragment(messages["stringProcess"]) {
    override val closeable = SimpleBooleanProperty(false)
    private val isRegexp = SimpleBooleanProperty(false)
    private val isSplitRegexp = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var taOutput: TextArea
    private lateinit var tfReplaceFrom: TextField
    private var replaceFromText
        get() = tfReplaceFrom.text.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
        set(value) {
            tfReplaceFrom.text = value
        }

    private lateinit var tfReplaceTo: TextField
    private var replaceToText
        get() = tfReplaceTo.text.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
        set(value) {
            tfReplaceTo.text = value
        }
    private lateinit var tfSplitLength: TextField
    private var splitLengthText
        get() =
            runCatching { tfSplitLength.text.toInt() }.getOrElse {
                tfSplitLength.text = "8"
                8
            }
        set(value) {
            tfSplitLength.text = value.toString()
        }

    private lateinit var tfSeprator: TextField
    private var sepratorText
        get() =
            tfSeprator
                .text
                .also { println(it) }
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .also { println("__${it}___") }
        set(value) {
            tfSeprator.text = value
        }

    private lateinit var labelInfo: Label
    private var timeConsumption = 0L
    private val info: String
        get() =
            " ${messages["inputLength"]}: " +
                "${inputText.length}  ${messages["outputLength"]}: ${outputText.length} " +
                "lines(in/out): ${inputText.lineCount()} / ${outputText.lineCount()} " +
                "cost: $timeConsumption ms"
    private var inputText: String
        get() =
            taInput.text.takeIf {
                isEncode || encodeType in arrayOf(EncodeType.Decimal, EncodeType.Octal)
            }
                ?: taInput.text.stripAllSpace()
        set(value) {
            taInput.text = value
        }
    private var outputText: String
        get() = taOutput.text
        set(value) {
            taOutput.text = value
        }
    private lateinit var tfExtract: TextField
    private var extractReg
        get() = tfExtract.text.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
        set(value) {
            tfExtract.text = value
        }
    private var encodeType = EncodeType.Base64
    private var isEncode = true

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 10 * 1024 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 10M"
            }
    }

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            button(graphic = imageview("/img/import.png")) {
                action {
                    inputText = clipboardText()
                    timeConsumption = 0
                    labelInfo.text = info
                }
            }
            button(graphic = imageview("/img/uppercase.png")) {
                action { outputText = inputText.uppercase() }
            }

            button(graphic = imageview("/img/lowercase.png")) {
                action { outputText = inputText.lowercase() }
            }
            button(graphic = imageview("/img/trimIndent.png")) {
                action { outputText = inputText.trimIndent() }
            }
            button(graphic = imageview("/img/ascend.png")) {
                action {
                    measureTimeMillis {
                        outputText =
                            inputText
                                .split("\n|\r\n".toRegex())
                                .sorted()
                                .joinToString(System.lineSeparator())
                    }
                        .also {
                            timeConsumption = it
                            labelInfo.text = info
                        }
                }
            }
            button(graphic = imageview("/img/descend.png")) {
                action {
                    measureTimeMillis {
                        outputText =
                            inputText
                                .split("\n|\r\n".toRegex())
                                .sortedDescending()
                                .joinToString(System.lineSeparator())
                    }
                        .also {
                            timeConsumption = it
                            labelInfo.text = info
                        }
                }
            }

            button(graphic = imageview("/img/deduplicate.png")) {
                action {
                    measureTimeMillis {
                        outputText =
                            inputText
                                .split("\n|\r\n".toRegex())
                                .distinct()
                                .joinToString(System.lineSeparator())
                    }
                        .also {
                            timeConsumption = it
                            labelInfo.text = info
                        }
                }
            }
            button(graphic = imageview("/img/statistic.png")) {
                action {
                    measureTimeMillis {
                        outputText =
                            inputText
                                .fold(mutableMapOf<Char, Int>()) { acc, c ->
                                    acc.apply { acc[c] = (acc[c] ?: 0) + 1 }
                                }
                                .toList()
                                .joinToString(System.lineSeparator()) {
                                    "${it.first}: ${it.second}"
                                }
                    }
                        .also {
                            timeConsumption = it
                            labelInfo.text = info
                        }
                }
            }
        }

        taInput =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
                contextmenu {
                    item(messages["loadFromNet"]) {
                        action { runAsync { inputText.readFromNet() } ui { taInput.text = it } }
                    }
                    item(messages["loadFromNet2"]) {
                        action {
                            runAsync { inputText.readBytesFromNet().base64() } ui
                                {
                                    taInput.text = it
                                }
                        }
                    }
                    item(messages["readHeadersFromNet"]) {
                        action {
                            runAsync { inputText.readHeadersFromNet() } ui { taInput.text = it }
                        }
                    }

                    item(messages["recoverEncoding"]) {
                        action { runAsync { inputText.recoverEncoding() } ui { taInput.text = it } }
                    }
                    item("reverse") { action { taInput.text = inputText.reversed() } }
                }
                textProperty().addListener { _, _, _ ->
                    timeConsumption = 0
                    labelInfo.text = info
                }
            }
        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label(messages["replace"])
            tfReplaceFrom = textfield { promptText = messages["text2Replaced"] }
            tfReplaceTo = textfield { promptText = messages["replaced"] }
            checkbox(messages["regexp"], isRegexp)
            button(messages["run"], imageview("/img/run.png")) { action { doReplace() } }
        }
        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label(messages["split"])
            tfSplitLength = textfield { promptText = messages["splitLength"] }
            tfSeprator = textfield { promptText = messages["delimiter"] }
            checkbox(messages["regexp"], isSplitRegexp) { isVisible = false }
            button(messages["run"], imageview("/img/run.png")) { action { doSplit() } }
        }

        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label(messages["extract"])
            tfExtract =
                textfield {
                    promptText = messages["extractHint"]
                    prefWidth = 330.0
                }
            checkbox(messages["regexp"]) { isVisible = false }
            button(messages["run"], imageview("/img/run.png")) { action { doExtract() } }
        }

        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
            button(graphic = imageview("/img/up.png")) {
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                }
            }
        }

        taOutput =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
            }
        subscribe<SimpleMsgEvent> { inputText = it.msg }
    }

    private fun doExtract() {
        measureTimeMillis {
            outputText = extractReg.toRegex().findAll(inputText).map { it.value }.joinToString("\n")
        }
            .also {
                timeConsumption = it
                labelInfo.text = info
            }
    }

    private fun doSplit() {

        measureTimeMillis {
            outputText =
                inputText.toList().chunked(splitLengthText).joinToString(sepratorText) {
                    it.joinToString("")
                }
        }
            .also {
                timeConsumption = it
                labelInfo.text = info
            }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doReplace() {
        measureTimeMillis {
            if (replaceFromText.isNotEmpty()) {
                println(replaceToText)
                outputText =
                    if (isRegexp.get()) inputText.replace(replaceFromText.toRegex(), replaceToText)
                    else inputText.replace(replaceFromText, replaceToText)
            }
        }
            .also {
                timeConsumption = it
                labelInfo.text = info
            }
    }
}
