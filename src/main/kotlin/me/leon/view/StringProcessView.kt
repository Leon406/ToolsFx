package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import me.leon.encode.base.base64
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.EncodeType
import me.leon.ext.clipboardText
import me.leon.ext.copy
import me.leon.ext.fileDraggedHandler
import me.leon.ext.readBytesFromNet
import me.leon.ext.readFromNet
import tornadofx.FX.Companion.messages
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.checkbox
import tornadofx.contextmenu
import tornadofx.get
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.item
import tornadofx.label
import tornadofx.paddingAll
import tornadofx.paddingBottom
import tornadofx.paddingTop
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.vbox

class StringProcessView : View(messages["stringProcess"]) {
    override val closeable = SimpleBooleanProperty(false)
    private val isRegexp = SimpleBooleanProperty(false)
    private val isSplitRegexp = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var output: TextArea
    private lateinit var replaceFrom: TextField
    private var replaceFromText
        get() = replaceFrom.text.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
        set(value) {
            replaceFrom.text = value
        }

    private lateinit var replaceTo: TextField
    private var replaceToText
        get() = replaceTo.text.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
        set(value) {
            replaceTo.text = value
        }
    private lateinit var splitLength: TextField
    private var splitLengthText
        get() =
            runCatching { splitLength.text.toInt() }.getOrElse {
                splitLength.text = "8"
                8
            }
        set(value) {
            splitLength.text = value.toString()
        }

    private lateinit var seprator: TextField
    private var sepratorText
        get() =
            seprator
                .text
                .also { println(it) }
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .also { println("__${it}___") }
        set(value) {
            seprator.text = value
        }

    private lateinit var infoLabel: Label
    private val info: String
        get() =
            " ${messages["inputLength"]}:" +
                " ${inputText.length}  ${messages["outputLength"]}: ${outputText.length}"
    private var inputText: String
        get() =
            input.text.takeIf {
                isEncode || encodeType in arrayOf(EncodeType.Decimal, EncodeType.Octal)
            }
                ?: input.text.replace("\\s".toRegex(), "")
        set(value) {
            input.text = value
        }
    private var outputText: String
        get() = output.text
        set(value) {
            output.text = value
        }

    private var encodeType = EncodeType.Base64
    private var isEncode = true

    private val eventHandler = fileDraggedHandler { input.text = it.first().readText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
            spacing = DEFAULT_SPACING
            button(graphic = imageview(Image("/import.png"))) {
                action { input.text = clipboardText() }
            }
            button(graphic = imageview(Image("/uppercase.png"))) {
                action { outputText = inputText.uppercase() }
            }
            button(graphic = imageview(Image("/lowercase.png"))) {
                action { outputText = inputText.lowercase() }
            }
            button(graphic = imageview(Image("/ascend.png"))) {
                action {
                    outputText =
                        inputText
                            .split("\n|\r\n".toRegex())
                            .sorted()
                            .joinToString(System.lineSeparator())
                }
            }
            button(graphic = imageview(Image("/descend.png"))) {
                action {
                    outputText =
                        inputText
                            .split("\n|\r\n".toRegex())
                            .sortedDescending()
                            .joinToString(System.lineSeparator())
                }
            }
        }

        input =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
                contextmenu {
                    item(messages["loadFromNet"]) {
                        action { runAsync { inputText.readFromNet() } ui { input.text = it } }
                    }
                    item(messages["loadFromNet2"]) {
                        action {
                            runAsync { inputText.readBytesFromNet().base64() } ui
                                {
                                    input.text = it
                                }
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
            replaceFrom = textfield { promptText = messages["text2Replaced"] }
            replaceTo = textfield { promptText = messages["replaced"] }
            checkbox(messages["regexp"], isRegexp)
            button(messages["run"], imageview(Image("/run.png"))) { action { doReplace() } }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label(messages["split"])
            splitLength = textfield { promptText = messages["splitLength"] }
            seprator = textfield { promptText = messages["seprator"] }
            checkbox(messages["regexp"], isSplitRegexp) { isVisible = false }
            button(messages["run"], imageview(Image("/run.png"))) { action { doSplit() } }
        }

        hbox {
            spacing = DEFAULT_SPACING
            label(messages["output"])
            button(graphic = imageview(Image("/copy.png"))) { action { outputText.copy() } }
            button(graphic = imageview(Image("/up.png"))) {
                action {
                    input.text = outputText
                    output.text = ""
                }
            }
        }

        output =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
            }
    }

    private fun doSplit() {
        outputText =
            inputText.toList().chunked(splitLengthText).joinToString(sepratorText) {
                it.joinToString("")
            }
        infoLabel.text = info
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { infoLabel = label(info) }
    }

    private fun doReplace() {
        if (replaceFromText.isNotEmpty()) {
            println(replaceToText)
            outputText =
                if (isRegexp.get()) inputText.replace(replaceFromText.toRegex(), replaceToText)
                else inputText.replace(replaceFromText, replaceToText)
        }
        infoLabel.text = info
    }
}
