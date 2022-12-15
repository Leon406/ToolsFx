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

    private var timeConsumption = 0L
    private var startTime = 0L
    private var encodeType = EncodeType.BASE64
    private var isEncode = true
    private val excludeEncode =
        arrayOf(
            EncodeType.RADIX8,
            EncodeType.BASE16,
            EncodeType.OCTAL,
            //            EncodeType.Decimal,
            EncodeType.RADIX9,
            EncodeType.RADIX_N,
            EncodeType.RADIX10,
            EncodeType.RADIX32,
            EncodeType.RADIX64,
            EncodeType.UTF7,
            EncodeType.UTF7_ALL,
            EncodeType.UTF7_EXT,
            EncodeType.BASE69,
            EncodeType.BASE65536,
            EncodeType.ECOJI,
            EncodeType.BASE2048,
            EncodeType.BASE32768,
        )
    private val encodeTypeWithSpace =
        arrayOf(
            EncodeType.UUENCODE,
            EncodeType.XXENCODE,
            EncodeType.QUOTE_PRINTABLE,
            EncodeType.PUNY_CODE,
            EncodeType.BASE69,
            EncodeType.BASE65536,
        )

    override val closeable = SimpleBooleanProperty(false)
    private val singleLine = SimpleBooleanProperty(false)
    private val fileMode = SimpleBooleanProperty(false)
    private val decodeIgnoreSpace = SimpleBooleanProperty(true)
    private val processing = SimpleBooleanProperty(false)
    private val enableDict = SimpleBooleanProperty(true)
    private val selectedCharset = SimpleStringProperty(CHARSETS.first())

    private var taInput: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var tfCustomDict: TextField by singleAssign()
    private var tfCount: TextField by singleAssign()

    private val times
        get() = tfCount.text.toIntOrNull() ?: 1.also { tfCount.text = it.toString() }

    private val info: String
        get() =
            "${if (isEncode) messages["encode"] else messages["decode"]}: $encodeType  ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length} " +
                "count: $times cost: $timeConsumption ms"
    private val inputText: String
        get() =
            taInput.text.takeIf {
                isEncode || encodeType in arrayOf(EncodeType.DECIMAL, EncodeType.OCTAL)
            }
                ?: taInput.text.takeUnless { decodeIgnoreSpace.get() }
                    ?: taInput.text.stripAllSpace()
    private val outputText: String
        get() = taOutput.text

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            with(it.first()) {
                if (fileMode.get()) {
                    this.absolutePath
                } else {
                    properText()
                }
            }
    }

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["input"])
            addClass(Styles.left)
            spacing = DEFAULT_SPACING
            button(graphic = imageview(IMG_NEW_WINDOW)) {
                tooltip(messages["newWindow"])
                action { find<EncodeView>().openWindow() }
            }
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taInput.text = clipboardText() }
            }
            checkbox(messages["singleLine"], singleLine) {
                selectedProperty().addListener { _, _, newValue ->
                    decodeIgnoreSpace.set(!newValue)
                }
            }
            checkbox(messages["decodeIgnoreSpace"], decodeIgnoreSpace)
            checkbox(messages["fileMode"], fileMode)
        }

        taInput = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = eventHandler
            prefRowCount = TEXT_AREA_LINES
            contextmenu {
                item(messages["reverse"]) { action { taInput.text = inputText.reversed() } }

                item(messages["loadFromNet"]) {
                    action { runAsync { inputText.readFromNet() } ui { taInput.text = it } }
                }
                item(messages["loadFromNetLoop"]) {
                    action { runAsync { inputText.simpleReadFromNet() } ui { taInput.text = it } }
                }
                item(messages["loadFromNet2"]) {
                    action {
                        runAsync { inputText.readBytesFromNet().base64() } ui { taInput.text = it }
                    }
                }
                item(messages["readHeadersFromNet"]) {
                    action { runAsync { inputText.readHeadersFromNet() } ui { taInput.text = it } }
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
                            if (it.value == EncodeType.BASE64) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        encodeType = new.cast<RadioButton>().text.encodeType()
                        enableDict.value =
                            encodeType.type.contains("base") &&
                                !BASE_ENCODE_EXCLUDED_DICT_LIST.contains(encodeType) ||
                                encodeType == EncodeType.RADIX_N
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
                label("times:") {
                    tooltip("encode or decode  times \nor crack minimum result length")
                }
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
            button(messages["run"], imageview(IMG_RUN)) {
                enableWhen(!processing)
                action { run() }
            }
            button("crack", imageview(IMG_CRACK)) {
                enableWhen(!processing)
                action { crack() }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { outputText.copy() }
            }
            button(graphic = imageview("/img/jump.png")) {
                tooltip(messages["goStringProcess"])
                action {
                    fire(SimpleMsgEvent(taOutput.text, 1))
                    val tabPane = findParentOfType(TabPane::class)
                    tabPane
                        ?.selectionModel
                        ?.select(tabPane.tabs.first { it.text == messages["stringProcess"] })
                }
            }
            button(graphic = imageview(IMG_UP)) {
                tooltip(messages["up"])
                action {
                    taInput.text =
                        outputText.takeUnless { it.contains(REG_CRACK_HEADER) }
                            ?: outputText
                                .lines()
                                .filterNot { it.contains(REG_CRACK_HEADER) }
                                .joinToString(System.lineSeparator())
                    taOutput.text = ""
                }
            }
        }

        taOutput = textarea {
            promptText = messages["outputHint"]
            isWrapText = true
            prefRowCount = TEXT_AREA_LINES
        }
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun run() {

        var result = inputText
        runAsync {
            processing.value = true
            startTime = System.currentTimeMillis()

            runCatching {
                    if (times > 40) kotlin.error("times should be not large than 40")
                    repeat(times) {
                        result =
                            if (isEncode) {
                                controller.encode2String(
                                    result,
                                    encodeType,
                                    tfCustomDict.text,
                                    selectedCharset.get(),
                                    singleLine.get()
                                )
                            } else {
                                controller.decode2String(
                                    result,
                                    encodeType,
                                    tfCustomDict.text,
                                    selectedCharset.get(),
                                    singleLine.get()
                                )
                            }
                    }
                    result
                }
                .getOrElse { it.stacktrace() }
        } ui
            {
                processing.value = false
                taOutput.text = it
                if (Prefs.autoCopy) {
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
                }
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }
    }

    private fun crack() {
        startTime = System.currentTimeMillis()
        var encoded =
            if (fileMode.get()) {
                taInput.text.toFile().readText()
            } else {
                taInput.text.substringAfter("\t")
            }
        processing.value = true
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
                            runCatching { controller.decode2String(encoded, encode, "") }
                                .getOrElse { it.message }!!
                                .also {
                                    if (DEBUG) {
                                        println(
                                            "after decode:${System.currentTimeMillis() - startTime} " +
                                                "$encode ${System.currentTimeMillis() - start}"
                                        )
                                    }
                                }
                    }
                    .find {
                        (it.second.isEmpty() ||
                                it.second.contains(encoded, true) ||
                                it.second.contains(REG_NON_PRINTABLE) ||
                                it.first == EncodeType.URL_ENCODE.type &&
                                    encoded.length == it.second.length ||
                                it.second.length < times)
                            .not()
                    }
                    ?.run {
                        encodeMethods.add(first)
                        encoded = second
                    }
                    ?: break
            }
            encodeMethods.mapIndexed { i, type -> "${i + 1} $type" }.joinToString("-->")
        } ui
            {
                processing.value = false
                taOutput.text =
                    if (encodeMethods.isNotEmpty()) "$it\n$encoded" else "no crack result!!!"
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
                // 手动gc, 立即回收创建的临时字符串
                System.gc()
            }
    }
}
