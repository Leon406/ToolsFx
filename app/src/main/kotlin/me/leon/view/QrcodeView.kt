package me.leon.view

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCombination
import javafx.scene.text.Text
import me.leon.*
import me.leon.ext.*
import me.leon.ext.fx.*
import tornadofx.*

class QrcodeView : Fragment("Qrcode") {

    private val errorLvs = listOf("L ~7%", "M ~15%", "Q ~25%", "H ~30%")
    private val selectedErrLv = SimpleStringProperty(errorLvs.first())
    override val closeable = SimpleBooleanProperty(false)

    private var button: Button by singleAssign()
    private var ta: TextArea by singleAssign()
    private var textCount: Text by singleAssign()

    // 切成的图片展示区域
    private var iv: ImageView by singleAssign()

    private val eventHandler = fileDraggedHandler {
        ta.text =
            runCatching { it.joinToString("\n") { "${it.name}:    ${it.qrReader()}" } }
                .getOrElse { it.stacktrace() }
    }

    override val root = vbox {
        paddingAll = DEFAULT_SPACING_2X
        spacing = DEFAULT_SPACING_2X
        hbox {
            spacing = DEFAULT_SPACING_2X
            label(messages["recognize"])
            button =
                button(messages["shotReco"]) {
                    action { this@QrcodeView.show() }
                    shortcut(KeyCombination.valueOf("Ctrl+Q"))
                    tooltip("快捷键Ctrl+Q")
                }

            button(messages["clipboardReco"]) {
                action { clipboardImage()?.toBufferImage()?.qrReader()?.let { ta.text = it } }
            }
            button(messages["fileReco"]) {
                shortcut(KeyCombination.valueOf("Ctrl+F"))
                tooltip("快捷键Ctrl+F，也可以手动拖动多个文件到输入输入框")
                action {
                    primaryStage.multiFileChooser(messages["chooseFile"])?.let {
                        if (it.size == 1) {
                            iv.image = Image(it.first().inputStream())
                            ta.text = it.first().qrReader()
                        } else {
                            ta.text =
                                runCatching {
                                        it.joinToString("\n") { "${it.name}:    ${it.qrReader()}" }
                                    }
                                    .getOrElse { it.stacktrace() }
                        }
                    }
                }
            }
        }

        hbox {
            spacing = DEFAULT_SPACING_3X
            label(messages["content"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { ta.text.copy().also { if (it) primaryStage.showToast("复制成功") } }
            }
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { ta.text = clipboardText() }
            }
        }
        vbox {
            alignment = Pos.CENTER_RIGHT
            spacing = DEFAULT_SPACING
            ta = textarea {
                promptText = messages["qrHint"]
                isWrapText = true
                prefHeight = DEFAULT_SPACING_10X
                onDragEntered = eventHandler
                textProperty().addListener { _, _, newValue ->
                    println(newValue.length)
                    textCount.text = "text count: ${newValue.length}"
                }
            }

            textCount = text("text count: 0")
        }

        hbox {
            spacing = DEFAULT_SPACING_2X
            addClass(Styles.left)

            combobox(selectedErrLv, errorLvs)
            button(messages["genQrcode"]) {
                action {
                    if (ta.text.isNotEmpty()) {
                        runCatching { iv.image = createQR(ta.text, selectedErrLv.get().errLevel()) }
                            .onFailure { primaryStage.showToast(it.message ?: "unknown exception") }
                    }
                }
                shortcut(KeyCombination.valueOf("F9"))
                tooltip("快捷键F9")
            }
        }
        hbox {
            label(messages["qrImg"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { iv.image?.copy()?.also { if (it) primaryStage.showToast("复制二维码成功") } }
            }
        }
        hbox {
            alignment = Pos.CENTER
            iv = imageview()
        }
    }

    private fun show() {
        primaryStage.screenShot {
            if (it == null) {
                primaryStage.showToast("unrecognized")
            } else {
                val bufferedImage = it.toFxImg()
                iv.image = bufferedImage
                runAsync { runCatching { it.qrReader() }.getOrElse { it.stacktrace() } } ui
                    {
                        selectThisTab()
                        ta.text = it
                    }
            }
        }
    }

    private fun selectThisTab() {
        root.findParentOfType(TabPane::class)?.run {
            val thisTab = tabs.first { it.text == title }
            if (selectionModel.selectedItem != thisTab) {
                selectionModel.select(thisTab)
            }
        }
    }

    private fun createQR(
        data: String = "this is test data",
        errLv: ErrorCorrectionLevel = ErrorCorrectionLevel.L
    ): Image {
        return data.createQR(errorCorrectionLevel = errLv).toFxImg()
    }

    private fun String.errLevel() =
        when {
            startsWith("L") -> ErrorCorrectionLevel.L
            startsWith("M") -> ErrorCorrectionLevel.M
            startsWith("Q") -> ErrorCorrectionLevel.Q
            startsWith("H") -> ErrorCorrectionLevel.H
            else -> ErrorCorrectionLevel.L
        }
}
