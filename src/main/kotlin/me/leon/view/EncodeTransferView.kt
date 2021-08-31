package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import me.leon.controller.EncodeController
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.EncodeType
import me.leon.ext.copy
import me.leon.ext.encodeType
import tornadofx.*

class EncodeTransferView : View("编码转换") {
    private val controller: EncodeController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var output: TextArea
    private lateinit var infoLabel: Label
    private val info: String
        get() =
            " $srcEncodeType --> $dstEncodeType  输入长度: ${inputText.length}  输出长度: ${outputText.length}"
    private val inputText: String
        get() = input.text.takeIf { isEncode } ?: input.text.replace("\\s".toRegex(), "")
    private val outputText: String
        get() = output.text

    private var dstEncodeType = EncodeType.UrlEncode
    private var srcEncodeType = EncodeType.Base64
    private var isEncode = true

    private val eventHandler =
        EventHandler<DragEvent> {
            println("${it.dragboard.hasFiles()}______" + it.eventType)
            if (it.eventType.name == "DRAG_ENTERED") {
                if (it.dragboard.hasFiles()) {
                    println(it.dragboard.files)
                    input.text = it.dragboard.files.first().readText()
                }
            }
        }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            label("待处理:")
            alignment = Pos.CENTER_LEFT
            togglegroup {
                spacing = DEFAULT_SPACING
                radiobutton("base64") { isSelected = true }
                radiobutton("urlEncode")
                radiobutton("base32")
                radiobutton("base16")
                radiobutton("unicode")
                radiobutton("hex")
                radiobutton("binary")
                radiobutton("urlBase64")
                radiobutton("base58")
                radiobutton("base58Check")
                selectedToggleProperty().get()
                selectedToggleProperty().addListener { _, _, new ->
                    srcEncodeType = (new as RadioButton).text.encodeType()
                }
            }
        }
        input =
            textarea {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }

        tilepane {
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING *2
            alignment = Pos.CENTER
            button("转换") {
                action { run() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button("上移⬆") {
                action {
                    input.text = outputText
                    output.text = ""
                }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button("复制结果") {
                action { outputText.copy() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
        }
        hbox {
            label("输出内容:")
            alignment = Pos.CENTER_LEFT
            togglegroup {
                spacing = DEFAULT_SPACING
                radiobutton("base64")
                radiobutton("urlEncode") { isSelected = true }
                radiobutton("base32")
                radiobutton("base16")
                radiobutton("unicode")
                radiobutton("hex")
                radiobutton("binary")
                radiobutton("urlBase64")
                radiobutton("base58")
                radiobutton("baseCheck")
                selectedToggleProperty().addListener { _, _, new ->
                    dstEncodeType = (new as RadioButton).text.encodeType()
                    run()
                }
            }
        }

        output =
            textarea {
                promptText = "结果"
                isWrapText = true
            }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { infoLabel = label() }
    }

    private fun run() {
        val decode = controller.decode(inputText, srcEncodeType)
        output.text =
            if (String(decode, Charsets.UTF_8).contains("解码错误:")) {
                String(decode, Charsets.UTF_8)
            } else {
                controller.encode2String(decode, dstEncodeType)
            }
        infoLabel.text = info
    }
}
