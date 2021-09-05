package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import me.leon.controller.EncodeController
import me.leon.ext.*
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

    private val eventHandler = fileDraggedHandler { input.text = it.first().readText() }

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING

        hbox {
            label("待处理:")
            button(graphic = imageview(Image("/import.png"))) {
                action { input.text = clipboardText() }
            }
        }

        input =
            textarea {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label("编码:")
            tilepane {
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                togglegroup {
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
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("urlEncode") {
                        tooltip(
                            "url编码解码,又叫百分号编码，是统一资源定位(URL)编码方式。\n" +
                                "URL地址（常说网址）规定了常用地数字，字母可以直接使用，另外一批作为\n" +
                                "特殊用户字符也可以直接用（/,:@等），剩下的其它所有字符必须通过%xx编码处理。 "
                        )
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }

                    radiobutton("unicode") {
                        tooltip(
                            "统一码，也叫万国码、单一码（Unicode）是计算机科学领域里的一项业界标准，包括字符集、编码方案等。\n" +
                                "Unicode 是为了解决传统的字符编码方案的局限而产生的，它为每种语言中的每个字符设定了统一并且唯一的二进制编码，\n" +
                                "以满足跨语言、跨平台进行文本转换、处理的要求。"
                        )
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("hex") {
                        tooltip("16进制0123456789ABCDEF 表示")
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("binary") {
                        tooltip("二进制 01表示")
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("urlBase64") {
                        tooltip(
                            "base64传统编码中会出现+, /两个会被url直接转义的符号，因此如果希望通过url传输这些编码字符串，我们\n" +
                                "需要先做base64编码，随后将+和/分别替换为- _两个字符"
                        )
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("base16") {
                        tooltip(
                            "Base16编码使用16个ASCII可打印字符（数字0-9和字母A-F）对任意字节数据进行编码。\n" +
                                "Base16先获取输入字符串每个字节的二进制值（不足8比特在高位补0），然后将其串联进来，\n" +
                                "再按照4比特一组进行切分，将每组二进制数分别转换成十进制"
                        )
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("base32") {
                        tooltip(
                            "Base32编码是使用32个可打印字符（字母A-Z和数字2-7）对任意字节数据进行编码的方案，\n" +
                                "编码后的字符串不用区分大小写并排除了容易混淆的字符，可以方便地由人类使用并由计算机处理。"
                        )
                        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                    }
                    radiobutton("base36") { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }
                    radiobutton("base58") { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }
                    radiobutton("base58Check") { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }
                    radiobutton("base62") { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }
                    radiobutton("base85") { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }
                    radiobutton("base91") { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }

                    selectedToggleProperty().addListener { _, _, new ->
                        encodeType = (new as RadioButton).text.encodeType()
                        if (isEncode) {
                            output.text = controller.encode2String(inputText, encodeType)
                            infoLabel.text = info
                        }
                    }
                }
            }
        }
        hbox {
            spacing = DEFAULT_SPACING
            togglegroup {
                spacing = DEFAULT_SPACING
                alignment = Pos.BASELINE_CENTER
                radiobutton("编码") { isSelected = true }
                radiobutton("解码")
                selectedToggleProperty().addListener { _, _, new ->
                    isEncode = (new as RadioButton).text == "编码"
                    run()
                }
            }
            button("运行", imageview(Image("/run.png"))) { action { run() } }
        }
        hbox {
            spacing = DEFAULT_SPACING
            label("输出内容:")
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
                promptText = "结果"
                isWrapText = true
            }
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { infoLabel = label(info) }
    }

    private fun run() {
        if (isEncode) {
            output.text = controller.encode2String(inputText, encodeType)
        } else {
            output.text = controller.decode2String(inputText, encodeType)
        }
        infoLabel.text = info
    }
}
