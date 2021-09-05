package me.leon.view

import java.awt.Rectangle
import java.awt.Robot
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.HBox
import javafx.scene.paint.Paint
import javafx.stage.Stage
import javafx.stage.StageStyle
import me.leon.ext.*
import kotlin.math.abs
import tornadofx.*

class QrcodeView : View("Qrcode") {
    // 切图区域的起始位置x
    private var startX = 0.0

    // 切图区域的起始位置y
    private var startY = 0.0

    // 切图区域宽
    private var w = 0.0

    // 切图区域高
    private var h = 0.0

    // 切图区域
    private lateinit var hBox: HBox
    private lateinit var bu: Button
    private lateinit var tf: TextArea

    // 切成的图片展示区域
    private lateinit var iv: ImageView

    override val closeable = SimpleBooleanProperty(false)

    override val root = vbox {
        paddingAll = DEFAULT_SPACING_2X
        spacing = DEFAULT_SPACING_2X
        hbox {
            spacing = DEFAULT_SPACING_2X
            label("识别：")
            bu =
                button("截屏识别") {
                    action { this@QrcodeView.show() }
                    shortcut(KeyCombination.valueOf("Ctrl+Q"))
                    tooltip("快捷键Ctrl+Q")
                }

            button("剪贴板图片") {
                action { clipboardImage()?.toBufferImage()?.qrReader()?.let { tf.text = it } }
            }
            button("文件识别") {
                shortcut(KeyCombination.valueOf("Ctrl+F"))
                tooltip("快捷键Ctrl+F")
                action {
                    primaryStage.fileChooser()?.let {
                        iv.image = Image(it.inputStream())
                        tf.text = it.qrReader()
                    }
                }
            }
        }

        hbox {
            spacing = DEFAULT_SPACING_3X
            label("内容:")
            button(graphic = imageview(Image("/copy.png"))) {
                action { tf.text.copy().also { if (it) primaryStage.showToast("复制成功") } }
            }
            button(graphic = imageview(Image("/import.png"))) {
                action { tf.text = clipboardText() }
            }
        }
        tf =
            textarea {
                promptText = "请输入文本或者使用截屏识别/识别二维码"
                isWrapText = true
                prefHeight = DEFAULT_SPACING_10X
            }

        hbox {
            spacing = DEFAULT_SPACING_2X
            label("生成：")
            button("生成二维码") {
                action {
                    if (tf.text.isNotEmpty()) {
                        iv.image = createQR(tf.text)
                    }
                }
                shortcut(KeyCombination.valueOf("F9"))
                tooltip("快捷键F9")
            }
        }
        hbox {
            label("二维码图片:")
            button(graphic = imageview(Image("/copy.png"))) {
                action { iv.image?.copy()?.also { if (it) primaryStage.showToast("复制二维码成功") } }
            }
        }
        hbox {
            alignment = Pos.CENTER
            iv = imageview()
        }
    }

    private fun show() {
        // 将主舞台缩放到任务栏
        primaryStage.isIconified = true
        // 创建辅助舞台，并设置场景与布局
        val stage = Stage()
        // 锚点布局采用半透明
        val anchorPane = AnchorPane()
        anchorPane.style = "-fx-background-color: #85858522"
        // 场景设置白色全透明
        val scene = Scene(anchorPane)
        scene.fill = Paint.valueOf("#ffffff00")
        stage.scene = scene
        // 清楚全屏中间提示文字
        stage.fullScreenExitHint = ""
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.isFullScreen = true
        stage.show()

        // 切图窗口绑定鼠标按下事件
        anchorPane.onMousePressed =
            EventHandler { event: MouseEvent ->
                // 清除锚点布局中所有子元素
                anchorPane.children.clear()
                // 创建切图区域
                hBox =
                    HBox().apply {
                        background = null
                        border =
                            Border(
                                BorderStroke(
                                    Paint.valueOf("#c03700"),
                                    BorderStrokeStyle.SOLID,
                                    null,
                                    BorderWidths(2.0)
                                )
                            )
                    }
                anchorPane.children.add(hBox)
                // 记录并设置起始位置
                startX = event.sceneX
                startY = event.sceneY
                AnchorPane.setLeftAnchor(hBox, startX)
                AnchorPane.setTopAnchor(hBox, startY)
            }
        // 绑定鼠标按下拖拽的事件
        addMouseDraggedEvent(anchorPane)
        // 绑定鼠标松开事件
        addMouseReleasedEvent(anchorPane, stage)
        scene.onKeyPressed =
            EventHandler { event: KeyEvent ->
                if (event.code == KeyCode.ESCAPE) {
                    stage.close()
                    primaryStage.isIconified = false
                }
            }
    }

    private fun addMouseReleasedEvent(anchorPane: AnchorPane, stage: Stage) {
        anchorPane.onMouseReleased =
            EventHandler { event: MouseEvent ->
                // 记录最终长宽
                w = abs(event.sceneX - startX)
                h = abs(event.sceneY - startY)
                anchorPane.style = "-fx-background-color: #00000000"
                // 添加剪切按钮，并显示在切图区域的底部
                val b = Button("剪切")
                hBox.border =
                    Border(
                        BorderStroke(
                            Paint.valueOf("#85858544"),
                            BorderStrokeStyle.SOLID,
                            null,
                            BorderWidths(2.0)
                        )
                    )
                hBox.children.add(b)
                hBox.alignment = Pos.BOTTOM_RIGHT
                // 为切图按钮绑定切图事件
                b.onAction =
                    EventHandler {
                        // 切图辅助舞台
                        stage.close()
                        runCatching { captureImg() }.onFailure {
                            it.printStackTrace()
                            primaryStage.showToast("二维码识别错误")
                        }
                        // 主舞台还原
                        primaryStage.isIconified = false
                    }
            }
    }

    private fun addMouseDraggedEvent(anchorPane: AnchorPane) {
        anchorPane.onMouseDragged =
            EventHandler { event: MouseEvent ->
                // 用label记录切图区域的长宽
                val label =
                    Label().apply {
                        alignment = Pos.CENTER
                        prefHeight = DEFAULT_SPACING_4X
                        prefWidth = DEFAULT_SPACING_20X
                        textFill = Paint.valueOf("#ffffff") // 白色填充
                        style = "-fx-background-color: #000000" // 黑背景
                    }

                anchorPane.children.add(label)
                AnchorPane.setLeftAnchor(label, startX + DEFAULT_SPACING_4X)
                AnchorPane.setTopAnchor(label, startY)

                // 计算宽高并且完成切图区域的动态效果
                w = abs(event.sceneX - startX)
                h = abs(event.sceneY - startY)
                hBox.prefWidth = w
                hBox.prefHeight = h
                label.text = "宽：$w 高：$h"
            }
    }

    @Throws(Exception::class)
    fun captureImg() {
        val robot = Robot()
        val re = Rectangle(startX.toInt(), startY.toInt(), w.toInt(), h.toInt())
        val screenCapture = robot.createScreenCapture(re)
        val bufferedImage = screenCapture.toFxImg()
        iv.image = bufferedImage
        tf.text = screenCapture.qrReader()
    }

    private fun createQR(data: String = "this is test data"): Image {
        return data.createQR().toFxImg()
    }
}
