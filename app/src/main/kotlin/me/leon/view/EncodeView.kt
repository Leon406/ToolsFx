package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.*
import me.leon.controller.EncodeController
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.*
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class EncodeView : Fragment(messages["encodeAndDecode"]) {
    private val controller: EncodeController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
    private val decodeIgnoreSpace = SimpleBooleanProperty(true)
    private val isProcessing = SimpleBooleanProperty(false)
    private var taInput: TextArea by singleAssign()
    private lateinit var taOutput: TextArea
    private lateinit var labelInfo: Label
    private lateinit var tfCustomDict: TextField
    private lateinit var tfCount: TextField
    private var enableDict = SimpleBooleanProperty(true)
    private var timeConsumption = 0L
    private var startTime = 0L
    private val times
        get() =
            tfCount.text.toIntOrNull()?.takeIf { it <= 40 }
                ?: 1.also { tfCount.text = it.toString() }

    private val info: String
        get() =
            "${if (isEncode) messages["encode"] else messages["decode"]}: $encodeType  ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length} " +
                "count: $times cost: $timeConsumption ms"
    private val inputText: String
        get() =
            taInput.text.takeIf {
                isEncode || encodeType in arrayOf(EncodeType.Decimal, EncodeType.Octal)
            }
                ?: taInput.text.takeUnless { decodeIgnoreSpace.get() }
                    ?: taInput.text.stripAllSpace()
    private val outputText: String
        get() = taOutput.text

    private var encodeType = EncodeType.Base64
    private var isEncode = true
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (length() <= 10 * 1024 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 10M"
            }
    }

    private val encodeTypeWithSpace =
        arrayOf(
            EncodeType.UuEncode,
            EncodeType.XxEncode,
            EncodeType.QuotePrintable,
            EncodeType.PunyCode,
        )

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            button(graphic = imageview("/img/openwindow.png")) {
                tooltip(messages["newWindow"])
                action { find<EncodeView>().openWindow() }
            }
            button(graphic = imageview("/img/import.png")) {
                tooltip(messages["pasteFromClipboard"])
                action { taInput.text = clipboardText() }
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
                    item(messages["loadFromNetLoop"]) {
                        action {
                            runAsync { inputText.simpleReadFromNet() } ui { taInput.text = it }
                        }
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
                textProperty().addListener { _, _, _ -> labelInfo.text = info }
            }
        hbox {
            addClass(Styles.left)
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            label("${messages["encode"]}:")
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    encodeTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == EncodeType.Base64) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        encodeType = new.cast<RadioButton>().text.encodeType()
                        enableDict.value =
                            encodeType.type.contains("base") && encodeType.type != "base100"
                        tfCustomDict.text = encodeType.defaultDict
                        val isIgnore = encodeType !in encodeTypeWithSpace
                        decodeIgnoreSpace.set(isIgnore)
                        println("${decodeIgnoreSpace.get()} $isIgnore")
                        if (isEncode) run()
                    }
                }
            }
        }

        hbox {
            label(messages["customDict"])
            alignment = Pos.BASELINE_LEFT
            tfCustomDict =
                textfield(encodeType.defaultDict) {
                    enableWhen { enableDict }
                    prefWidth = DEFAULT_SPACING_80X
                }
        }

        hbox {
            spacing = DEFAULT_SPACING
            addClass(Styles.center)
            togglegroup {
                spacing = DEFAULT_SPACING
                label("charset:")
                combobox(selectedCharset, CHARSETS) { cellFormat { text = it } }

                radiobutton(messages["encode"]) { isSelected = true }
                radiobutton(messages["decode"])
                checkbox(messages["singleLine"], isSingleLine) {
                    selectedProperty().addListener { _, _, newValue ->
                        decodeIgnoreSpace.set(!newValue)
                    }
                }
                checkbox(messages["decodeIgnoreSpace"], decodeIgnoreSpace) {
                    selectedProperty().addListener { observable, oldValue, newValue ->
                        println("$observable $oldValue  $newValue")
                    }
                }

                label("times:")
                tfCount =
                    textfield("1") {
                        textFormatter = intTextFormatter
                        prefWidth = DEFAULT_SPACING_8X
                    }
                selectedToggleProperty().addListener { _, _, new ->
                    isEncode = new.cast<RadioButton>().text == messages["encode"]
                    run()
                }
            }
            button(messages["run"], imageview("/img/run.png")) {
                enableWhen(!isProcessing)
                action { run() }
            }
            button("crack", imageview("/img/crack.png")) {
                enableWhen(!isProcessing)
                action { crack() }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) {
                tooltip(messages["copy"])
                action { outputText.copy() }
            }
            button(graphic = imageview("/img/jump.png")) {
                tooltip(messages["goStringProcess"])
                action {
                    fire(SimpleMsgEvent(taOutput.text, 1))
                    val tabPane = findParentOfType(TabPane::class)
                    tabPane?.selectionModel?.select(
                        tabPane.tabs.first { it.text == messages["stringProcess"] }
                    )
                }
            }
            button(graphic = imageview("/img/up.png")) {
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
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun run() {

        var result = inputText
        runAsync {
            isProcessing.value = true
            startTime = System.currentTimeMillis()

            runCatching {
                repeat(times) {
                    result =
                        if (isEncode)
                            controller.encode2String(
                                result,
                                encodeType,
                                tfCustomDict.text,
                                selectedCharset.get(),
                                isSingleLine.get()
                            )
                        else
                            controller.decode2String(
                                result,
                                encodeType,
                                tfCustomDict.text,
                                selectedCharset.get(),
                                isSingleLine.get()
                            )
                }
                result
            }
                .getOrElse { it.stacktrace() }
        } ui
            {
                isProcessing.value = false
                taOutput.text = it
                if (Prefs.autoCopy)
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }
    }

    private val excludeEncode =
        arrayOf(
            EncodeType.Radix8,
            EncodeType.Base16,
            EncodeType.Decimal,
            EncodeType.Radix10,
            EncodeType.Radix32,
            EncodeType.Radix64,
        )

    private fun crack() {
        startTime = System.currentTimeMillis()
        var encoded = taInput.text.substringAfter("\t")
        isProcessing.value = true
        if (DEBUG) println("read ${System.currentTimeMillis() - startTime}")
        val encodeMethods = mutableListOf<String>()
        runAsync {
            while (true) {
                EncodeType.values()
                    .filterNot { it in excludeEncode }
                    .asSequence()
                    .map { encode ->
                        if (DEBUG) println("map $encode ${System.currentTimeMillis() - startTime}")
                        val start = System.currentTimeMillis()
                        encode.type to
                            kotlin
                                    .runCatching { controller.decode2String(encoded, encode, "") }
                                    .getOrElse { it.message }!!
                                .also {
                                    if (DEBUG)
                                        println(
                                            "after decode:${System.currentTimeMillis() - startTime} " +
                                                "$encode ${System.currentTimeMillis() - start}"
                                        )
                                }
                    }
                    .find {
                        (it.second.isEmpty() ||
                                it.second.contains(encoded, true) ||
                                it.second.contains("[\u0000-\u001F]|解码错误:|�".toRegex()) ||
                                it.first == EncodeType.UrlEncode.type &&
                                    encoded.length == it.second.length)
                            .not()
                    }
                    ?.run {
                        encodeMethods.add(first)
                        encoded = second
                    }
                    ?: break
            }
            encodeMethods.mapIndexed { i, type -> "${i + 1} $type" }.joinToString("-->") +
                "\n" +
                encoded
        } ui
            {
                isProcessing.value = false
                taOutput.text = it
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                // 手动gc, 立即回收创建的临时字符串
                System.gc()
            }
    }
}
