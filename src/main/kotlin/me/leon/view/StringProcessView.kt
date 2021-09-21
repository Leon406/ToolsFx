package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import kotlin.collections.chunked
import kotlin.collections.contains
import kotlin.collections.distinct
import kotlin.collections.first
import kotlin.collections.joinToString
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.sorted
import kotlin.collections.sortedDescending
import kotlin.collections.toList
import me.leon.SimpleMsgEvent
import me.leon.encode.base.base64
import me.leon.ext.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class StringProcessView : View(messages["stringProcess"]) {
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
    private val info: String
        get() =
            " ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length} " +
                "lines(input/output):${inputText.lineCount()} / ${outputText.lineCount()}"
    private var inputText: String
        get() =
            taInput.text.takeIf {
                isEncode || encodeType in arrayOf(EncodeType.Decimal, EncodeType.Octal)
            }
                ?: taInput.text.replace("\\s".toRegex(), "")
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

    private val eventHandler = fileDraggedHandler { taInput.text = it.first().readText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            button(graphic = imageview("/img/import.png")) {
                action {
                    inputText = clipboardText()
                    labelInfo.text = info
                }
            }
            button(graphic = imageview("/img/uppercase.png")) {
                action { outputText = inputText.uppercase() }
            }
            button(graphic = imageview("/img/lowercase.png")) {
                action { outputText = inputText.lowercase() }
            }
            button(graphic = imageview("/img/ascend.png")) {
                action {
                    outputText =
                        inputText
                            .split("\n|\r\n".toRegex())
                            .sorted()
                            .joinToString(System.lineSeparator())
                    labelInfo.text = info
                }
            }
            button(graphic = imageview("/img/descend.png")) {
                action {
                    outputText =
                        inputText
                            .split("\n|\r\n".toRegex())
                            .sortedDescending()
                            .joinToString(System.lineSeparator())
                    labelInfo.text = info
                }
            }

            button(graphic = imageview("/img/deduplicate.png")) {
                action {
                    outputText =
                        inputText
                            .split("\n|\r\n".toRegex())
                            .distinct()
                            .joinToString(System.lineSeparator())
                    labelInfo.text = info
                }
            }
            button(graphic = imageview("/img/statistic.png")) {
                action {
                    outputText =
                        inputText
                            .fold(mutableMapOf<Char, Int>()) { acc, c ->
                                acc.apply { acc[c] = (acc[c] ?: 0) + 1 }
                            }
                            .toList()
                            .joinToString(System.lineSeparator()) { "${it.first}: ${it.second}" }
                    labelInfo.text = info
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
                }
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label(messages["replace"])
            tfReplaceFrom = textfield { promptText = messages["text2Replaced"] }
            tfReplaceTo = textfield { promptText = messages["replaced"] }
            checkbox(messages["regexp"], isRegexp)
            button(messages["run"], imageview("/img/run.png")) { action { doReplace() } }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label(messages["split"])
            tfSplitLength = textfield { promptText = messages["splitLength"] }
            tfSeprator = textfield { promptText = messages["seprator"] }
            checkbox(messages["regexp"], isSplitRegexp) { isVisible = false }
            button(messages["run"], imageview("/img/run.png")) { action { doSplit() } }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
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
        outputText = extractReg.toRegex().findAll(inputText).map { it.value }.joinToString("\n")
        labelInfo.text = info
    }

    private fun doSplit() {
        outputText =
            inputText.toList().chunked(splitLengthText).joinToString(sepratorText) {
                it.joinToString("")
            }
        labelInfo.text = info
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doReplace() {
        if (replaceFromText.isNotEmpty()) {
            println(replaceToText)
            outputText =
                if (isRegexp.get()) inputText.replace(replaceFromText.toRegex(), replaceToText)
                else inputText.replace(replaceFromText, replaceToText)
        }
        labelInfo.text = info
    }
}
