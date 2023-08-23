package me.leon.view

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.concurrent.Task
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import javafx.util.Callback
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis
import me.leon.*
import me.leon.config.VOCABULARY_DIR
import me.leon.domain.SimpleMsgEvent
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.ext.ocr.BaiduOcr
import me.leon.ext.voice.*
import me.leon.misc.Translator
import tornadofx.*
import tornadofx.FX.Companion.messages

class StringProcessView : Fragment(messages["stringProcess"]) {

    private var timeConsumption = 0L
    private var dictType = "known"
    private var sourceDataline: SourceDataLine? = null
    private var stopTts: Boolean = false

    override val closeable = SimpleBooleanProperty(false)
    private val regexp = SimpleBooleanProperty(false)
    private val splitRegexp = SimpleBooleanProperty(false)
    private val fileMode = SimpleBooleanProperty(false)
    private val overrideInput = SimpleBooleanProperty(false)
    private val showAdditionInput = SimpleBooleanProperty(false)
    private val showList = SimpleBooleanProperty(false)
    private val additionFileMode = SimpleBooleanProperty(false)
    private val showAdditionUi = SimpleBooleanProperty(false)
    private val ignoreCase = SimpleBooleanProperty(false)
    private val enableTtsButton = SimpleBooleanProperty(true)

    private var taInput: TextArea by singleAssign()
    private var taInput2: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var tfReplaceFrom: TextField by singleAssign()
    private var tfReplaceTo: TextField by singleAssign()
    private var tfSplitLength: TextField by singleAssign()
    private var tfSeparator: TextField by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var tfExtract: TextField by singleAssign()
    private var lvVocabularyList: ListView<Vocabulary> by singleAssign()

    private var preWord: Pair<String, Int>? = null
    private val findInputPositionAction = { word: String -> locateWord(word) }

    private var ttsTask: Task<Unit>? = null

    private val syllables by lazy {
        val r = mutableMapOf<String, String>()
        val file = File(VOCABULARY_DIR, "syllable.txt")
        if (file.exists()) {
            file
                .readText()
                .lines()
                .filter { it.isNotEmpty() }
                .map {
                    val (w, s) = it.split("\t")
                    r[w] = s
                }
        }
        r
    }

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

    private val words = FXCollections.observableArrayList<Vocabulary>()
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
                    it.first().properText(1024 * 1024)
                }
            }
    }
    private val additionEventHandler = fileDraggedHandler {
        taInput2.text =
            if (additionFileMode.get()) {
                it.joinToString(System.lineSeparator()) { it.absolutePath }
            } else {
                it.first().properText()
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
                shortcut(KeyCombination.valueOf("Ctrl+I"))
                action {
                    selectThisTab()
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
                            .sortedBy {
                                if (ignoreCase.get()) {
                                    it.lowercase()
                                } else {
                                    it
                                }
                            }
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
                            .sortedByDescending {
                                if (ignoreCase.get()) {
                                    it.lowercase()
                                } else {
                                    it
                                }
                            }
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
            button(graphic = imageview(IMG_TTS)) {
                shortcut(KeyCombination.valueOf("Alt+V"))
                enableWhen(enableTtsButton)
                action {
                    taInput.requestFocus()
                    ttsTask?.cancel()
                    ttsTask = runAsync {
                        sourceDataline?.run {
                            stop()
                            stopTts = true
                        }

                        val text = taInput.selectedText.ifEmpty { taInput.text }
                        val start =
                            taInput.selection.start.takeIf { it != taInput.selection.end } ?: 0
                        if (Prefs.ttsLongSentence) {
                            speak(text.indices, start, text)
                        } else {
                            speakMulti(start, text)
                            println("____end")
                        }
                        stopTts = false
                        sourceDataline = null
                    }
                }
            }
            button(graphic = imageview("/img/ocr.png")) {
                shortcut(KeyCombination.valueOf("Ctrl+O"))
                action {
                    primaryStage.screenShot {
                        if (it == null) {
                            primaryStage.showToast("unrecognized")
                        } else {
                            selectThisTab()
                            runCatching {
                                    taInput.text = BaiduOcr.ocrBase64(it.toByteArray().base64())
                                }
                                .onFailure { taInput.text = it.stackTraceToString() }
                        }
                    }
                }
            }
            checkbox("override", overrideInput)
            checkbox("ignore case", ignoreCase)
            button(graphic = imageview("/img/settings.png")) {
                action { find<StringProcessConfigFragment>().openWindow() }
            }
        }

        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = eventHandler
            contextmenu {
                item(messages["loadFromNet"]) {
                    action { runAsync { inputText.readFromNet() } ui { taInput.text = it } }
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
                item("tokenize - addition (optional)", KeyCombination.valueOf("Ctrl+T")) {
                    action {
                        selectThisTab()
                        tokenize()
                    }
                }
                item("input - addition (key)") {
                    action {
                        val (inputs, inputs2) = inputsList()
                        taOutput.text =
                            inputs
                                .filterNot {
                                    val key =
                                        if (extractReg.isEmpty()) {
                                            it
                                        } else {
                                            it.replace(extractReg.toRegex(), "$1")
                                        }
                                    inputs2.any { it.equals(key, ignoreCase.get()) }
                                }
                                .joinToString(System.lineSeparator())
                        showAdditionInput.value = false
                        labelInfo.text = info
                    }
                }
                item("input ∩ addition (key)") {
                    action {
                        val (inputs, inputs2) = inputsList()
                        taOutput.text =
                            inputs
                                .filter {
                                    val key =
                                        if (extractReg.isEmpty()) {
                                            it
                                        } else {
                                            it.replace(extractReg.toRegex(), "$1")
                                        }
                                    inputs2.any { it.equals(key, ignoreCase.get()) }
                                }
                                .joinToString(System.lineSeparator())
                        showAdditionInput.value = false
                        labelInfo.text = info
                    }
                }
                item("input ∪ addition") {
                    action {
                        val (inputs, inputs2) = inputsList()
                        taOutput.text =
                            (inputs + inputs2).distinct().joinToString(System.lineSeparator())
                        showAdditionInput.value = false
                        labelInfo.text = info
                    }
                }
                item("duplicate", KeyCombination.valueOf("Ctrl+D")) {
                    action {
                        var targetEnd = taInput.selection.end
                        val duplicateText =
                            taInput.selectedText.ifEmpty {
                                with(inputText.lines()) {
                                    val i =
                                        inputText.substring(0, taInput.caretPosition).lines().size
                                    targetEnd = this.take(i).sumOf { it.length + 1 } - 1
                                    this[i - 1]
                                }
                            }
                        taInput.insertText(targetEnd, System.lineSeparator() + duplicateText)
                    }
                }

                item("translate", KeyCombination.valueOf("Alt+T")) {
                    action {
                        val text = taInput.selectedText.ifEmpty { taInput.text }
                        runAsync {
                            Translator.translate(text, target = Prefs.translateTargetLan)
                        } ui
                            {
                                Alert(Alert.AlertType.WARNING)
                                    .apply {
                                        title = "Translate"
                                        headerText = ""
                                        dialogPane.maxWidth = DEFAULT_SPACING_80X
                                        graphic =
                                            text(it).apply { wrappingWidth = DEFAULT_SPACING_80X }
                                    }
                                    .show()
                            }
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
                action {
                    if (showList.get()) {
                        lvVocabularyList.selectionModel.selectedItems
                            .joinToString(System.lineSeparator()) { it.word }
                            .copy()
                    } else {
                        outputText.copy()
                    }
                }
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
                checkbox("show addition", showAdditionInput) { visibleWhen(showAdditionUi) }
                checkbox("file mode", additionFileMode) { visibleWhen(showAdditionUi) }
                checkbox("list", showList) { visibleWhen(showAdditionUi) }
                button(graphic = imageview(IMG_ADD)) {
                    tooltip("only keep selected item")
                    visibleWhen(showList)
                    action {
                        val vocabularies = words - lvVocabularyList.selectionModel.selectedItems
                        writeVocabularyToFile(vocabularies)
                        words.removeAll(vocabularies)
                    }
                }
                button(graphic = imageview(IMG_REMOVE)) {
                    tooltip("delete selected item")
                    visibleWhen(showList)
                    shortcut(KeyCombination.valueOf("Ctrl+S"))
                    action {
                        writeVocabularyToFile(lvVocabularyList.selectionModel.selectedItems)
                        words.removeAll(lvVocabularyList.selectionModel.selectedItems)
                    }
                }
            }
            hbox {
                addClass(Styles.left)
                visibleWhen(showList)
                togglegroup {
                    radiobutton("known") { isSelected = true }
                    radiobutton("new")
                    radiobutton("exclusive")
                    selectedToggleProperty().addListener { _, _, new ->
                        dictType = new.cast<RadioButton>().text
                    }
                }
            }
        }
        stackpane {
            prefHeight = 300.0
            spacing = 8.0
            taOutput = textarea {
                vgrow = Priority.ALWAYS
                promptText = messages["outputHint"]
                isWrapText = true
            }

            taInput2 = textarea {
                promptText = "additional input or output"
                isWrapText = true
                visibleWhen(showAdditionInput)
                onDragEntered = additionEventHandler
            }
            lvVocabularyList =
                listview(words) {
                    visibleWhen(showList)
                    cellFactory = Callback {
                        VocabularyCell().apply { action = findInputPositionAction }
                    }
                    selectionModel.selectionMode = SelectionMode.MULTIPLE
                    setOnKeyPressed { keyEvent ->
                        if (keyEvent.code == KeyCode.ENTER) {
                            VocabularyCell.showWordInfo(selectionModel.selectedItems.first().word)
                        } else if (keyEvent.code == KeyCode.F1) {
                            locateWord(selectionModel.selectedItems.first().word)
                        }
                    }
                }

            showList.addListener { _, _, newValue ->
                if (newValue) {
                    showAdditionInput.value = false
                }
            }
            showAdditionInput.addListener { _, _, newValue ->
                if (newValue) {
                    showList.value = false
                }
            }
        }
        subscribe<SimpleMsgEvent> { inputText = it.msg }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun locateWord(word: String) {
        val text = taInput.text.lowercase()
        val sameWord = preWord?.first == word
        val fromIndex =
            if (sameWord) {
                preWord!!.second
            } else {
                0
            }
        var startIndex = text.indexOf(word, fromIndex)
        if (startIndex == -1) {
            startIndex = text.indexOf(word)
        }
        taInput.selectRange(startIndex, startIndex + word.length)
        preWord = word to startIndex + word.length
    }

    private fun speak(range: IntRange, start: Int, content: String) {
        enableTtsButton.value = false
        taInput.selectRange(range.first + start, range.last + start + 1)
        sourceDataline =
            tts(
                content,
                voiceModel = Prefs.ttsVoice,
                rate = Prefs.ttsSpeed,
                volume = Prefs.ttsVolume,
                pitch = Prefs.ttsPitch,
                cacheable = Prefs.ttsCacheable,
            )
        enableTtsButton.value = true
        while (sourceDataline?.isOpen == true) {
            // loop check
        }
    }

    private fun speakMulti(start: Int, content: String) {
        ttsMultiStream(
                content,
                voiceModel = Prefs.ttsVoice,
                rate = Prefs.ttsSpeed,
                volume = Prefs.ttsVolume,
                pitch = Prefs.ttsPitch,
                cacheable = Prefs.ttsCacheable,
            )
            ?.forEach {
                val (rangePair, bytes) = it
                val (range, _) = rangePair
                if (stopTts) {
                    return
                }
                println(it)
                taInput.selectRange(range.first + start, range.last + start + 1)
                sourceDataline = bytes?.run { Audio.play(this.inputStream(), true) }
                while (sourceDataline?.isOpen == true) {
                    // loop check
                }
            }
    }

    private fun writeVocabularyToFile(vocabularies: List<Vocabulary>) {
        val defaultFile = File(VOCABULARY_DIR, dictType)
        val targetFile =
            if (additionFileMode.get()) {
                inputText2
                    .lines()
                    .filter { it.isNotEmpty() }
                    .map { it.toFile() }
                    .firstOrNull { it.exists() && it.name.contains(dictType) }
                    ?: defaultFile
            } else {
                defaultFile
            }

        if (defaultFile == targetFile) {
            if (!inputText2.lines().contains(targetFile.absolutePath)) {
                inputText2 = "$inputText2${System.lineSeparator()}${defaultFile.absolutePath}"
            }
            additionFileMode.value = true
        }
        if (!targetFile.exists()) {
            targetFile.createNewFile()
        }
        println("==> write to $targetFile")
        val newWords = vocabularies.map { it.word } - targetFile.readText().lines().toSet()
        if (newWords.isNotEmpty()) {
            targetFile.appendText(newWords.joinToString(System.lineSeparator()))
            targetFile.appendText(System.lineSeparator())
        }
    }

    private fun inputsList(): Pair<Set<String>, Set<String>> {
        showAdditionUi.value = true
        val inputs = inputText.lines().map { it.trim() }.filterNot { it.isEmpty() }.toSet()
        val inputs2 = inputs2()
        return Pair(inputs, inputs2)
    }

    private fun inputs2(): Set<String> {
        val inputs2 =
            if (additionFileMode.get()) {
                inputText2
                    .lines()
                    .map { it.toFile() }
                    .filter { it.exists() }
                    .map { it.readText().lines().map { it.trim().lowercase() } }
                    .flatten()
            } else {
                inputText2.lines().map { it.trim() }
            }
        return inputs2.filterNot { it.isEmpty() }.toSet()
    }

    private fun tokenize() {
        inputText = inputText.ifEmpty { clipboardText() }.normalCharacter()

        showAdditionUi.value = true
        words.clear()
        val tokens =
            (inputText
                    .replace("[^a-zA-Z'-]+".toRegex(), "\n")
                    .lines()
                    .map { it.lowercase().trim('\'') }
                    .distinct()
                    .sorted() - inputs2())
                .filterNot { it.isEmpty() || !it.first().isLetter() || it.endsWith("'s") }
                .also {
                    words.addAll(
                        it.map { token ->
                            Vocabulary(token, ToolsApp.vocabulary[token]).apply {
                                syllable = syllables[token]
                            }
                        }
                    )
                    thread {
                        val outOfDict =
                            File(VOCABULARY_DIR, "outOfDict.txt").also {
                                if (!it.exists()) {
                                    it.createNewFile()
                                }
                            }
                        val newWords =
                            words.filter { it.mean.isNullOrEmpty() }.map { it.word } -
                                outOfDict.readText().lines().toSet()
                        if (newWords.isNotEmpty()) {
                            outOfDict.appendText(
                                newWords.joinToString(System.lineSeparator()) +
                                    System.lineSeparator()
                            )
                        }
                    }
                }
                .joinToString(System.lineSeparator())
        if (overrideInput.get()) {
            taInput.text = tokens
        } else {
            taOutput.text = tokens
        }
        showList.value = true
        labelInfo.text = info
        lvVocabularyList.requestFocus()
    }

    private fun selectThisTab() {
        root.findParentOfType(TabPane::class)?.run {
            val thisTab = tabs.first { it.text == title }
            if (selectionModel.selectedItem != thisTab) {
                selectionModel.select(thisTab)
            }
        }
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
