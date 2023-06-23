package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import kotlin.system.measureTimeMillis
import me.leon.*
import me.leon.ext.*
import me.leon.ext.fx.clipboardText
import me.leon.ext.fx.copy
import me.leon.ext.fx.fileDraggedHandler
import tornadofx.*
import tornadofx.FX.Companion.messages

class StringProcessView : Fragment(messages["stringProcess"]) {

    private var timeConsumption = 0L
    override val closeable = SimpleBooleanProperty(false)
    private val regexp = SimpleBooleanProperty(false)
    private val splitRegexp = SimpleBooleanProperty(false)
    private val fileMode = SimpleBooleanProperty(false)
    private val overrideInput = SimpleBooleanProperty(false)
    private val showAddition = SimpleBooleanProperty(false)
    private val additionfileMode = SimpleBooleanProperty(false)
    private val showAdditionCheck = SimpleBooleanProperty(false)

    private var taInput: TextArea by singleAssign()
    private var taInput2: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var tfReplaceFrom: TextField by singleAssign()
    private var tfReplaceTo: TextField by singleAssign()
    private var tfSplitLength: TextField by singleAssign()
    private var tfSeparator: TextField by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var tfExtract: TextField by singleAssign()

    private val replaceFromText
        get() = tfReplaceFrom.text.unescape()

    private val replaceToText
        get() = tfReplaceTo.text.unescape()

    private val splitLengthText
        get() =
            runCatching { tfSplitLength.text.toInt() }
                .getOrElse {
                    tfSplitLength.text = "8"
                    8
                }

    private val separatorText
        get() = tfSeparator.text.unescape()

    private val info: String
        get() =
            " ${messages["inputLength"]}: " +
                "${inputText.length}  ${messages["outputLength"]}: ${outputText.length} " +
                "lines(in/out): ${inputText.lineCount()} / ${outputText.lineCount()} " +
                "cost: $timeConsumption ms"

    private var inputText: String
        get() = taInput.text
        set(value) {
            taInput.text = value
        }

    private var inputText2: String
        get() = taInput2.text
        set(value) {
            taInput2.text = value
        }

    private var outputText: String
        get() = taOutput.text
        set(value) {
            taOutput.text = value
        }

    private val extractReg
        get() = tfExtract.text.unescape()

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (fileMode.get()) {
                    absolutePath
                } else {
                    it.first().properText()
                }
            }
    }
    private val additionEventHandler = fileDraggedHandler {
        taInput2.text =
            with(it.first()) {
                if (additionfileMode.get()) {
                    it.map { it.absolutePath }.joinToString(System.lineSeparator())
                } else {
                    it.first().properText()
                }
            }
    }
    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            addClass(Styles.left)
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action {
                    inputText = clipboardText()
                    timeConsumption = 0
                    labelInfo.text = info
                }
            }
            button(graphic = imageview("/img/uppercase.png")) {
                tooltip(messages["uppercase"])
                action { processInput(inputText.uppercase()) }
            }

            button(graphic = imageview("/img/lowercase.png")) {
                tooltip(messages["lowercase"])
                action { processInput(inputText.lowercase()) }
            }
            button(graphic = imageview("/img/trimIndent.png")) {
                tooltip(messages["trimIndent"])
                action { processInput(inputText.trimIndent()) }
            }
            button(graphic = imageview("/img/ascend.png")) {
                tooltip(messages["orderByStringASC"])
                action {
                    processInput(
                        inputText
                            .split("\n|\r\n".toRegex())
                            .sorted()
                            .joinToString(System.lineSeparator())
                    )
                }
            }
            button(graphic = imageview("/img/descend.png")) {
                tooltip(messages["orderByStringDESC"])
                action {
                    processInput(
                        inputText
                            .split("\n|\r\n".toRegex())
                            .sortedDescending()
                            .joinToString(System.lineSeparator())
                    )
                }
            }

            button(graphic = imageview("/img/deduplicate.png")) {
                tooltip(messages["deduplicateLine"])
                action {
                    processInput(inputText.lines().distinct().joinToString(System.lineSeparator()))
                }
            }
            button(graphic = imageview("/img/statistic.png")) {
                tooltip(messages["letterStatistics"])
                action {
                    processInput(
                        inputText
                            .groupingBy { it }
                            .eachCount()
                            .toList()
                            .filter { it.first.code > 32 }
                            .sortedByDescending { it.second }
                            .joinToString(System.lineSeparator()) { "${it.first}: ${it.second}" }
                    )
                }
            }

            checkbox("override", overrideInput)
        }

        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = eventHandler
            contextmenu {
                item(messages["loadFromNet"]) {
                    action { runAsync { inputText.readFromNet() } ui { taInput.text = it } }
                }
                item(messages["recoverEncoding"]) {
                    action { runAsync { inputText.recoverEncoding() } ui { inputText = it } }
                }
                item(messages["reverse"]) { action { inputText = inputText.reversed() } }
                item(messages["removeAllSpaceByLine"]) {
                    action {
                        inputText =
                            inputText
                                .lines()
                                .map { it.stripAllSpace() }
                                .filterNot { it.isEmpty() }
                                .joinToString(System.lineSeparator())
                    }
                }
                item(messages["removeAllSpace"]) {
                    action { inputText = inputText.stripAllSpace() }
                }
                item("tokenize") { action { tokenize() } }
                item("tokenize - addition") { action { tokenize2() } }
                item("input - addition") {
                    action {
                        val (inputs, inputs2) = inputsList()
                        taOutput.text =
                            inputs
                                .filterNot { inputs2.contains(it) }
                                .joinToString(System.lineSeparator())
                        showAddition.value = false
                        labelInfo.text = info
                    }
                }

                item("input ∪ addition") {
                    action {
                        val (inputs, inputs2) = inputsList()
                        taOutput.text =
                            (inputs + inputs2).distinct().joinToString(System.lineSeparator())
                        showAddition.value = false
                        labelInfo.text = info
                    }
                }
                item("input ∩ addition") {
                    action {
                        val (inputs, inputs2) = inputsList()
                        taOutput.text =
                            inputs
                                .filter { inputs2.contains(it) }
                                .joinToString(System.lineSeparator())
                        showAddition.value = false
                        labelInfo.text = info
                    }
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
            checkbox(messages["regexp"], regexp)
            button(messages["run"], imageview(IMG_RUN)) { action { doReplace() } }
            checkbox(messages["fileMode"], fileMode)
        }
        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label(messages["split"])
            tfSplitLength = textfield { promptText = messages["splitLength"] }
            tfSeparator = textfield { promptText = messages["delimiter"] }
            checkbox(messages["regexp"], splitRegexp)
            button(messages["run"], imageview(IMG_RUN)) { action { doSplit() } }
        }

        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label(messages["extract"])
            tfExtract = textfield {
                promptText = messages["extractHint"]
                prefWidth = 330.0
            }
            checkbox(messages["regexp"]) { isVisible = false }
            button(messages["run"], imageview(IMG_RUN)) { action { doExtract() } }
            checkbox(messages["fileMode"], fileMode)
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

            hbox {
                addClass(Styles.left)
                checkbox("show addition", showAddition) { visibleWhen(showAdditionCheck) }
                checkbox("file mode", additionfileMode) { visibleWhen(showAdditionCheck) }
            }
        }
        stackpane {
            prefHeight = 300.0
            spacing = 8.0
            taInput2 = textarea {
                promptText = "additional input or output"
                isWrapText = true
                visibleWhen(showAddition)
                onDragEntered = additionEventHandler
            }
            taOutput = textarea {
                promptText = messages["outputHint"]
                visibleWhen(!showAddition)
                isWrapText = true
            }
        }
        subscribe<SimpleMsgEvent> { inputText = it.msg }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun inputsList(): Pair<List<String>, List<String>> {
        showAdditionCheck.value = true
        val inputs = inputText.lines().map { it.stripAllSpace() }.filterNot { it.isEmpty() }
        val inputs2 = inputs2()
        return Pair(inputs, inputs2)
    }

    private fun inputs2(): List<String> {
        val inputs2 =
            if (additionfileMode.get()) {
                inputText2
                    .lines()
                    .map { it.toFile() }
                    .filter { it.exists() }
                    .map { it.readText().lines().map { it.stripAllSpace() } }
                    .flatten()
                    .filterNot { it.isEmpty() }
                    .distinct()
            } else {
                inputText2.lines().map { it.stripAllSpace() }.filterNot { it.isEmpty() }
            }
        return inputs2
    }

    private fun tokenize() {
        showAdditionCheck.value = true
        val tokens =
            inputText
                .replace("[\\W\\d]+".toRegex(), "\n")
                .lines()
                .map { it.lowercase() }
                .distinct()
                .sorted()
                .joinToString(System.lineSeparator())
        if (overrideInput.get()) {
            taInput.text = tokens
        } else {
            taOutput.text = tokens
        }
        showAddition.value = true
        labelInfo.text = info
    }

    private fun tokenize2() {
        showAdditionCheck.value = true
        val tokens =
            (inputText
                    .replace("[\\W\\d]+".toRegex(), "\n")
                    .lines()
                    .map { it.lowercase() }
                    .distinct()
                    .sorted() - inputs2())
                .joinToString(System.lineSeparator())
        if (overrideInput.get()) {
            taInput.text = tokens
        } else {
            taOutput.text = tokens
        }
        showAddition.value = false
        labelInfo.text = info
    }

    private fun processInput(text: String) {
        measureTimeMillis {
                if (overrideInput.get()) {
                    inputText = text
                } else {
                    outputText = text
                }
            }
            .also {
                timeConsumption = it
                labelInfo.text = info
            }
    }

    private fun doExtract() {
        processInput(
            extractReg
                .toRegex()
                .findAll(if (fileMode.get()) inputText.toFile().readText() else inputText)
                .map { it.value }
                .joinToString("\n")
        )
    }

    private fun doSplit() {
        processInput(
            if (splitRegexp.get()) {
                inputText.split(tfSplitLength.text.unescape().toRegex()).joinToString(separatorText)
            } else {
                inputText.asIterable().chunked(splitLengthText).joinToString(separatorText) {
                    it.joinToString("")
                }
            }
        )
    }

    private fun doReplace() {
        if (replaceFromText.isNotEmpty()) {
            processInput(
                if (fileMode.get()) {
                    renameFiles()
                } else {
                    replaceStr(inputText)
                }
            )
        }
    }

    private fun replaceStr(name: String) =
        if (regexp.get()) {
            name.replace(replaceFromText.toRegex(), replaceToText)
        } else {
            name.replace(replaceFromText, replaceToText)
        }

    private fun renameFiles(): String {
        return inputText
            .lineAction {
                val file = it.toFile()
                if (file.exists().not()) {
                    return "$it file not exists!"
                }
                if (file.isDirectory) {
                    file
                        .walk()
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
