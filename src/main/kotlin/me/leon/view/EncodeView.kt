package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import me.leon.controller.EncodeController
import me.leon.ext.EncodeType
import me.leon.ext.copy
import me.leon.ext.encodeType
import tornadofx.*

class EncodeView : View("编解码") {
    private val controller: EncodeController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var output: TextArea
    private lateinit var infoLabel: Label
    private val info: String
        get() =
            "${if (isEncode) "编码" else "解码"}: $encodeType  输入长度: ${inputText.length}  输出长度: ${outputText.length}"
    private val inputText: String
        get() = input.text.takeIf { isEncode } ?: input.text.replace("\\s".toRegex(), "")
    private val outputText: String
        get() = output.text

    private var encodeType = EncodeType.Base64
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
    override val root = vbox {
        paddingAll = 8
        label("待处理:") { paddingAll = 8 }
        input =
            textarea {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            paddingAll = 8
            alignment = Pos.BASELINE_CENTER
            togglegroup {
                spacing = 8.0
                radiobutton("base64") {
                    isSelected = true
                    tooltip(
                        "Base64是一种基于64个可打印字符来表示二进制数据的表示方法!\n" +
                            "它是一种将二进制编码转换为可打印字符一种。它是MIME编码里面非常常见一种可逆转\n" +
                            "换二进制方法！\n" +
                            "由于2的6次方等于64，所以每6个位为一个单元，对应某个可打印字符。三个字节有24个位元，\n" +
                            "可以对应4个Base64单元，因此3个字节需要用4个base64单元来表示！ 这64个可打印字符a-z,A-Z,\n" +
                            "0-9就占62字符，剩下2个字符不同系统可能使用不同，\n" +
                            "经常是:“+/”。base64编码后，文档大小为原先的4/3，里面所有字节（包括常见可打印字符）也编码了！\n"
                    )
                }
                radiobutton("urlEncode") {
                    tooltip(
                        "url编码解码,又叫百分号编码，是统一资源定位(URL)编码方式。\n" +
                            "URL地址（常说网址）规定了常用地数字，字母可以直接使用，另外一批作为\n" +
                            "特殊用户字符也可以直接用（/,:@等），剩下的其它所有字符必须通过%xx编码处理。 "
                    )
                }
                radiobutton("base32") {
                    tooltip(
                        "Base32编码是使用32个可打印字符（字母A-Z和数字2-7）对任意字节数据进行编码的方案，\n" +
                            "编码后的字符串不用区分大小写并排除了容易混淆的字符，可以方便地由人类使用并由计算机处理。"
                    )
                }
                radiobutton("base16") {
                    tooltip(
                        "Base16编码使用16个ASCII可打印字符（数字0-9和字母A-F）对任意字节数据进行编码。\n" +
                            "Base16先获取输入字符串每个字节的二进制值（不足8比特在高位补0），然后将其串联进来，\n" +
                            "再按照4比特一组进行切分，将每组二进制数分别转换成十进制"
                    )
                }
                radiobutton("unicode") {
                    tooltip(
                        "统一码，也叫万国码、单一码（Unicode）是计算机科学领域里的一项业界标准，包括字符集、编码方案等。\n" +
                            "Unicode 是为了解决传统的字符编码方案的局限而产生的，它为每种语言中的每个字符设定了统一并且唯一的二进制编码，\n" +
                            "以满足跨语言、跨平台进行文本转换、处理的要求。"
                    )
                }
                radiobutton("hex") { tooltip("16进制0123456789ABCDEF 表示") }
                radiobutton("binary") { tooltip("二进制 01表示") }
                radiobutton("base64 safe") {
                    tooltip(
                        "base64传统编码中会出现+, /两个会被url直接转义的符号，因此如果希望通过url传输这些编码字符串，我们\n" +
                            "需要先做base64编码，随后将+和/分别替换为- _两个字符"
                    )
                }
                selectedToggleProperty().addListener { _, _, new ->
                    encodeType = (new as RadioButton).text.encodeType()
                    if (isEncode) {
                        output.text = controller.encode(inputText, encodeType)
                        infoLabel.text = info
                    }
                }
            }
        }
        hbox {
            togglegroup {
                spacing = 8.0
                alignment = Pos.BASELINE_CENTER
                radiobutton("编码") { isSelected = true }
                radiobutton("解码")
                selectedToggleProperty().addListener { _, _, new ->
                    isEncode = (new as RadioButton).text == "编码"
                    run()
                }
            }
            button("运行") { action { run() } }
            button("上移") {
                action {
                    input.text = outputText
                    output.text = ""
                }
            }
            button("复制结果") { action { outputText.copy() } }
        }
        label("输出内容:") { paddingBottom = 8 }
        output =
            textarea {
                promptText = "结果"
                isWrapText = true
            }

        infoLabel = label { paddingTop = 8 }
    }

    private fun run() {
        if (isEncode) {
            output.text = controller.encode(inputText, encodeType)
        } else {
            output.text = controller.decode(inputText, encodeType)
        }
        infoLabel.text = info
    }
}
