package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.*
import kotlin.collections.set
import kotlin.system.measureTimeMillis
import me.leon.*
import me.leon.ext.*
import me.leon.ext.crypto.EncodeType
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class StringProcessView : Fragment(messages["stringProcess"]) {

    private var timeConsumption = 0L
    private var encodeType = EncodeType.Base64
    private var isEncode = true

    override val closeable = SimpleBooleanProperty(false)
    private val isRegexp = SimpleBooleanProperty(false)
    private val isSplitRegexp = SimpleBooleanProperty(false)
    private val isFileMode = SimpleBooleanProperty(false)

    private var taInput: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var tfReplaceFrom: TextField by singleAssign()
    private var tfReplaceTo: TextField by singleAssign()
    private var tfSplitLength: TextField by singleAssign()
    private var tfSeprator: TextField by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var tfExtract: TextField by singleAssign()

    private var replaceFromText
        get() = tfReplaceFrom.text.unescape()
        set(value) {
            tfReplaceFrom.text = value
        }

    private var replaceToText
        get() = tfReplaceTo.text.unescape()
        set(value) {
            tfReplaceTo.text = value
        }

    private var splitLengthText
        get() =
            runCatching { tfSplitLength.text.toInt() }.getOrElse {
                tfSplitLength.text = "8"
                8
            }
        set(value) {
            tfSplitLength.text = value.toString()
        }

    private var sepratorText
        get() = tfSeprator.text.unescape().also { println("__${it}___") }
        set(value) {
            tfSeprator.text = value
        }

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
    private var extractReg
        get() = tfExtract.text.unescape()
        set(value) {
            tfExtract.text = value
        }

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (isFileMode.get()) {
                    absolutePath
                } else if (length() <= 10 * 1024 * 1024) {
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                } else "not support file larger than 10M"
            }
    }
    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            button(graphic = imageview("/img/import.png")) {
                tooltip(messages["pasteFromClipboard"])
                action {
                    inputText = clipboardText()
                    timeConsumption = 0
                    labelInfo.text = info
                }
            }
            button(graphic = imageview("/img/uppercase.png")) {
                tooltip(messages["uppercase"])
                action { outputText = inputText.uppercase() }
            }

            button(graphic = imageview("/img/lowercase.png")) {
                tooltip(messages["lowercase"])
                action { outputText = inputText.lowercase() }
            }
            button(graphic = imageview("/img/trimIndent.png")) {
                tooltip(messages["trimIndent"])
                action { outputText = inputText.trimIndent() }
            }
            button(graphic = imageview("/img/ascend.png")) {
                tooltip(messages["orderByStringASC"])
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
                tooltip(messages["orderByStringDESC"])
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
                tooltip(messages["deduplicateLine"])
                action {
                    measureTimeMillis {
                        outputText =
                            inputText.lines().distinct().joinToString(System.lineSeparator())
                    }
                        .also {
                            timeConsumption = it
                            labelInfo.text = info
                        }
                }
            }
            button(graphic = imageview("/img/statistic.png")) {
                tooltip(messages["letterStatistics"])
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
                    item(messages["recoverEncoding"]) {
                        action { runAsync { inputText.recoverEncoding() } ui { taInput.text = it } }
                    }
                    item(messages["reverse"]) { action { taInput.text = inputText.reversed() } }
                    item(messages["removeAllSpaceByLine"]) {
                        action {
                            taInput.text =
                                inputText
                                    .lines()
                                    .map { it.stripAllSpace() }
                                    .filterNot { it.isEmpty() }
                                    .joinToString(System.lineSeparator())
                        }
                    }
                    item(messages["removeAllSpace"]) {
                        action { taInput.text = inputText.stripAllSpace() }
                    }
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
            button(messages["run"], imageview(IMG_RUN)) { action { doReplace() } }
            checkbox(messages["fileMode"], isFileMode)
        }
        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label(messages["split"])
            tfSplitLength = textfield { promptText = messages["splitLength"] }
            tfSeprator = textfield { promptText = messages["delimiter"] }
            checkbox(messages["regexp"], isSplitRegexp) { isVisible = false }
            button(messages["run"], imageview(IMG_RUN)) { action { doSplit() } }
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
            button(messages["run"], imageview(IMG_RUN)) { action { doExtract() } }
            checkbox(messages["fileMode"], isFileMode)
        }

        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { outputText.copy() }
            }
            button(graphic = imageview(IMG_UP)) {
                tooltip(messages["up"])
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
    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doExtract() {
        measureTimeMillis {
            outputText =
                extractReg
                    .toRegex()
                    .findAll(if (isFileMode.get()) inputText.toFile().readText() else inputText)
                    .map { it.value }
                    .joinToString("\n")
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

    private fun doReplace() {
        measureTimeMillis {
            if (replaceFromText.isNotEmpty()) {
                println(replaceToText)
                outputText = if (isFileMode.get()) renameFiles() else replaceStr(inputText)
            }
        }
            .also {
                timeConsumption = it
                labelInfo.text = info
            }
    }

    private fun replaceStr(name: String) =
        if (isRegexp.get()) name.replace(replaceFromText.toRegex(), replaceToText)
        else name.replace(replaceFromText, replaceToText)

    private fun renameFiles(): String {
        return inputText
            .lineAction {
                val file = it.toFile()
                if (file.exists().not()) {
                    return "$it file not exists!"
                }
                if (file.isDirectory) {
                    file.walk()
                        .filter(File::isFile)
                        .filter { it.name != replaceStr(it.name) }
                        .map { f ->
                            File(f.parent, replaceStr(f.name)).also { f.renameTo(it) }.absolutePath
                        }
                        .joinToString(System.lineSeparator())
                } else {
                    File(file.parent, replaceStr(file.name)).also { file.renameTo(it) }.absolutePath
                }
            }
            .joinToString(System.lineSeparator())
    }
}
