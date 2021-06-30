package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import me.leon.ext.copy
import me.leon.ext.encodeType
import tornadofx.*

class Home : View("编解码加解密工具") {
    override val root = tabpane {
        tab<EncodeView>()
        tab<DigestView>()
        tab<SymmetricCryptoView>()
        tab<AsymmetricCryptoView>()
    }
}

class EncodeView : View("编解码") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    lateinit var input: TextArea
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text

    private var encodeType: EncodeType = EncodeType.Base64
    private var isEncode = true

    private val eventHandler = EventHandler<DragEvent> {
        println("${it.dragboard.hasFiles()}______" + it.eventType)
        if (it.eventType.name == "DRAG_ENTERED") {
            if (it.dragboard.hasFiles()) {
                println(it.dragboard.files)
                input.text = it.dragboard.files.first().readText()
            }
        }
    }
    override val root = vbox {
        paddingAll = 8

        label("待处理:") {
            paddingAll = 8
        }

        input = textarea {
            promptText = "请输入待编码内容或者拖动待编码的文本到此区域"
            isWrapText = true
            onDragEntered = eventHandler
        }
        hbox {
            paddingAll = 8
            togglegroup {
                spacing = 8.0
                radiobutton("base64") {
                    isSelected = true
                    tooltip("Base64是一种基于64个可打印字符来表示二进制数据的表示方法!\n" +
                            "它是一种将二进制编码转换为可打印字符一种。它是MIME编码里面非常常见一种可逆转\n" +
                            "换二进制方法！\n" +
                            "由于2的6次方等于64，所以每6个位为一个单元，对应某个可打印字符。三个字节有24个位元，\n" +
                            "可以对应4个Base64单元，因此3个字节需要用4个base64单元来表示！ 这64个可打印字符a-z,A-Z,\n" +
                            "0-9就占62字符，剩下2个字符不同系统可能使用不同，\n" +
                            "经常是:“+/”。base64编码后，文档大小为原先的4/3，里面所有字节（包括常见可打印字符）也编码了！\n"){
                        isWrapText = true
                    }
                }
                radiobutton("urlEncode") { }
                radiobutton("base32")
                radiobutton("base16")
                radiobutton("unicode")
                radiobutton("hex")
                radiobutton("binary")
                radiobutton("base64 safe") { }
                selectedToggleProperty().addListener { _, _, new ->
                    encodeType = (new as RadioButton).text.encodeType()
                    if (isEncode)
                        output.text = controller.encode(inputText, encodeType)
                }
            }
        }
        hbox {
            togglegroup {
                spacing = 8.0
                radiobutton("编码") {
                    isSelected = true
                }
                radiobutton("解码")
                selectedToggleProperty().addListener { _, _, new ->
                    isEncode = (new as RadioButton).text == "编码"
                    if (isEncode)
                        output.text = controller.encode(inputText, encodeType)
                    else
                        output.text = controller.decode(inputText, encodeType)
                }
            }
          button("运行") {
                action {
                    if (isEncode)
                        output.text = controller.encode(inputText, encodeType)
                    else
                        output.text = controller.decode(inputText, encodeType)
                }
            }

            button("上移") {
                action {
                    input.text = outputText
                    output.text = ""
                }
            }

            button("复制结果") {
                action {
                    outputText.copy()
                }
            }
        }
        label("输出内容:") {
            paddingBottom = 8
        }
        output = textarea {
            promptText = "结果"
            isWrapText = true
        }
    }
}

class DigestView : View("哈希") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    lateinit var input: TextArea
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text

    override val root = vbox {
        paddingAll = 8
        label("原文:") {
            paddingAll = 8
        }

        input = textarea {
            promptText = "请输入待编码内容"
            isWrapText = true

        }

        label("编码后内容:") {
            paddingBottom = 8
        }
        output = textarea {
            promptText = "处理后内容"
            isWrapText = true
        }
    }
}

class SymmetricCryptoView : View("对称加密") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    lateinit var input: TextArea
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text

    override val root = vbox {
        paddingAll = 8
        label("原文:") {
            paddingAll = 8
        }

        input = textarea {
            promptText = "请输入待编码内容"
            isWrapText = true

        }
        label("编码后内容:") {
            paddingBottom = 8
        }
        output = textarea {
            promptText = "处理后内容"
            isWrapText = true
        }
    }
}

class AsymmetricCryptoView : View("非对称加密") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    lateinit var input: TextArea
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text

    override val root = vbox {
        paddingAll = 8
        label("原文:") {
            paddingAll = 8
        }

        input = textarea {
            promptText = "请输入待编码内容"
            isWrapText = true

        }

        label("编码后内容:") {
            paddingBottom = 8
        }
        output = textarea {
            promptText = "处理后内容"
            isWrapText = true
        }
    }
}