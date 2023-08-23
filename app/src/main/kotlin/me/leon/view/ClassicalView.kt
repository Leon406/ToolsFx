package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import me.leon.*
import me.leon.config.TEXT_AREA_LINES
import me.leon.config.WIKI_CTF
import me.leon.controller.ClassicalController
import me.leon.ext.*
import me.leon.ext.crypto.*
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class ClassicalView : Fragment(messages["classical"]) {
    private val controller: ClassicalController by inject()

    private var timeConsumption = 0L
    private var startTime = 0L
    private var isEncrypt = true
    private var encodeType = ClassicalCryptoType.CAESAR

    override val closeable = SimpleBooleanProperty(false)
    private val singleLine = SimpleBooleanProperty(false)
    private val decodeIgnoreSpace = SimpleBooleanProperty(encodeType.isIgnoreSpace())
    private val param1Enabled = SimpleBooleanProperty(encodeType.paramsCount() > 0)
    private val param2Enabled = SimpleBooleanProperty(encodeType.paramsCount() > 1)
    private val checkbox1Hidden = SimpleBooleanProperty(encodeType.checkboxHintsCount() == 0)
    private val checkbox2Hidden = SimpleBooleanProperty(encodeType.checkboxHintsCount() < 2)
    private val processing = SimpleBooleanProperty(false)
    private val hasCrack = SimpleBooleanProperty(encodeType.hasCrack())

    private var taInput: TextArea by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var tfParam1: TextField by singleAssign()
    private var tfParam2: TextField by singleAssign()
    private var cb1: CheckBox by singleAssign()
    private var cb2: CheckBox by singleAssign()
    private var tfCrackKey: TextField by singleAssign()
    private var labelInfo: Label by singleAssign()

    private val info: String
        get() =
            "${if (isEncrypt) messages["encode"] else messages["decode"]}: $encodeType  ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length} cost: $timeConsumption ms"

    private val inputText: String
        get() = taInput.text.takeUnless { decodeIgnoreSpace.get() } ?: taInput.text.stripAllSpace()

    private val outputText: String
        get() = taOutput.text

    private val cryptoParams
        get() =
            mapOf(
                P1 to tfParam1.text,
                P2 to tfParam2.text,
                C1 to cb1.isSelected.toString(),
                C2 to cb2.isSelected.toString()
            )

    private val eventHandler = fileDraggedHandler { taInput.text = it.first().properText() }
    private val eventHandlerParam1 = fileDraggedHandler { tfParam1.text = it.first().absolutePath }
    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            spacing = DEFAULT_SPACING
            label(messages["input"])
            addClass(Styles.left)
            button(graphic = imageview(IMG_NEW_WINDOW)) {
                tooltip(messages["newWindow"])
                action { find<ClassicalView>().openWindow() }
            }
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taInput.text = clipboardText() }
            }
            button(graphic = imageview("/img/more.png")) {
                action { find<SymbolSubstitutionFragment>().openWindow() }
            }

            checkbox(messages["singleLine"], singleLine)
            checkbox(messages["decodeIgnoreSpace"], decodeIgnoreSpace)
        }

        taInput = textarea {
            prefRowCount = TEXT_AREA_LINES
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = eventHandler

            contextmenu {
                item(messages["reverse"]) { action { taInput.text = inputText.reversed() } }
            }
        }
        hbox {
            addClass(Styles.left)
            label("${messages["encrypt"]}:")
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 7
                togglegroup {
                    classicalTypeMap
                        .filter { if (ToolsApp.offlineMode) !it.key.contains("online") else true }
                        .forEach {
                            radiobutton(it.key) {
                                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                                if (it.value == ClassicalCryptoType.CAESAR) isSelected = true
                            }
                        }
                    selectedToggleProperty().addListener { _, _, new ->
                        encodeType = new.cast<RadioButton>().text.classicalType()
                        hasCrack.value = encodeType.hasCrack()
                        param1Enabled.set(encodeType.paramsCount() > 0)
                        param2Enabled.set(encodeType.paramsCount() > 1)
                        checkbox1Hidden.set(encodeType.checkboxHintsCount() == 0)
                        checkbox2Hidden.set(encodeType.checkboxHintsCount() < 2)
                        if (param1Enabled.get()) {
                            tfParam1.promptText = encodeType.paramsHints()[0]
                        }
                        if (param2Enabled.get()) {
                            tfParam2.promptText = encodeType.paramsHints()[1]
                        }
                        if (!checkbox1Hidden.get()) {
                            cb1.text = encodeType.checkboxHints()[0]
                        }
                        if (!checkbox2Hidden.get()) {
                            cb2.text = encodeType.checkboxHints()[1]
                        }
                        decodeIgnoreSpace.set(encodeType.isIgnoreSpace())

                        if (isEncrypt) {
                            run()
                        } else {
                            timeConsumption = 0
                            labelInfo.text = info
                        }
                    }
                }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.BASELINE_CENTER

            cb1 =
                checkbox(
                    if (encodeType.checkboxHintsCount() > 0) encodeType.checkboxHints()[0] else ""
                ) {
                    removeWhen(checkbox1Hidden)
                }
            cb2 =
                checkbox(
                    if (encodeType.checkboxHintsCount() > 1) encodeType.checkboxHints()[1] else ""
                ) {
                    removeWhen(checkbox2Hidden)
                }
            tfParam1 = textfield {
                prefWidth = DEFAULT_SPACING_50X
                promptText = encodeType.paramsHints()[0]
                onDragEntered = eventHandlerParam1
                visibleWhen(param1Enabled)
            }
            tfParam2 = textfield {
                prefWidth = DEFAULT_SPACING_50X
                promptText = encodeType.paramsHints()[1]
                visibleWhen(param2Enabled)
            }
        }

        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER
            togglegroup {
                radiobutton(messages["encrypt"]) { isSelected = true }
                radiobutton(messages["decrypt"])
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = new.cast<RadioButton>().text == messages["encrypt"]
                    run()
                }
            }
            button(messages["run"], imageview(IMG_RUN)) {
                action { run() }
                enableWhen(!processing)
            }
            button(messages["codeFrequency"]) { action { "https://quipqiup.com/".openInBrowser() } }

            button("wiki") { action { WIKI_CTF.openInBrowser() } }
            button("crack", imageview(IMG_CRACK)) {
                enableWhen(!processing)
                visibleWhen(hasCrack)
                action { crack() }
            }
            label("crack key:") { visibleWhen(hasCrack) }
            tfCrackKey =
                textfield("flag|ctf") {
                    visibleWhen(hasCrack)
                    prefWidth = DEFAULT_SPACING_8X
                }
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

        taOutput = textarea {
            vgrow = Priority.ALWAYS
            promptText = messages["outputHint"]
            isWrapText = true
            contextmenu {
                item("uppercase") { action { taOutput.text = taOutput.text.uppercase() } }
                item("lowercase") { action { taOutput.text = taOutput.text.lowercase() } }
                item("binary2hex") {
                    action { taOutput.text = taOutput.text.binary2ByteArray().toHex() }
                }
                item("reverse") {
                    action {
                        taOutput.text =
                            taOutput.text.split("\r\n|\n".toRegex()).joinToString("\r\n") {
                                it.reversed()
                            }
                    }
                }

                item("clear") { action { taOutput.text = "" } }
            }
        }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun run() {
        processing.value = true
        startTime = System.currentTimeMillis()
        runAsync {
            if (isEncrypt) {
                controller.encrypt(
                    inputText,
                    encodeType,
                    cryptoParams,
                    singleLine.get(),
                )
            } else {
                controller.decrypt(inputText, encodeType, cryptoParams, singleLine.get())
            }
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
        processing.value = true
        startTime = System.currentTimeMillis()
        runAsync {
            controller.crack(
                inputText,
                encodeType,
                tfCrackKey.text.takeUnless { it.isNullOrEmpty() } ?: "flag",
                singleLine.get(),
                cryptoParams
            )
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
}
