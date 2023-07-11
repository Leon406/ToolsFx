package me.leon.ext.fx

import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.math.abs
import me.leon.ext.DEFAULT_SPACING_20X
import me.leon.ext.DEFAULT_SPACING_4X
import tornadofx.*
import tornadofx.FX.Companion.messages

/**
 * @author Leon
 * @since 2023-07-11 16:32
 * @email deadogone@gmail.com
 */
class ScreenShot {
    private var startX = 0.0
    private var startY = 0.0
    private var w = 0.0
    private var h = 0.0

    // 切图区域
    private lateinit var hBox: HBox
    private lateinit var stage: Stage

    fun captureScreen(primeStage: Stage, callback: (BufferedImage?) -> Unit) {
        // 将主舞台缩放到任务栏
        primeStage.isIconified = true
        // 创建辅助舞台，并设置场景与布局

        // 锚点布局采用半透明
        val anchorPane = AnchorPane()
        anchorPane.style = "-fx-background-color: #85858522"
        // 场景设置白色全透明
        val scene =
            Scene(anchorPane).apply {
                fill = Paint.valueOf("#ffffff00")
                onKeyPressed = EventHandler { event: KeyEvent ->
                    if (event.code == KeyCode.ESCAPE) {
                        stage.close()
                        primeStage.isIconified = false
                    }
                }
            }
        stage =
            Stage().apply {
                this.scene = scene
                fullScreenExitHint = ""
                initStyle(StageStyle.TRANSPARENT)
                isFullScreen = true
            }
        stage.show()

        // 切图窗口绑定鼠标按下事件
        anchorPane.onMousePressed = EventHandler { event: MouseEvent ->
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
        addMouseReleasedEvent(primeStage, anchorPane, stage, callback)
    }

    private fun addMouseReleasedEvent(
        primaryStage: Stage,
        anchorPane: AnchorPane,
        stage: Stage,
        callback: (BufferedImage?) -> Unit
    ) {
        anchorPane.onMouseReleased = EventHandler { event: MouseEvent ->
            // 记录最终长宽
            w = abs(event.sceneX - startX)
            h = abs(event.sceneY - startY)
            anchorPane.style = "-fx-background-color: #00000000"
            // 添加剪切按钮，并显示在切图区域的底部
            val b = Button(messages["cut"])
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
            b.onAction = EventHandler {
                // 切图辅助舞台
                stage.close()
                runCatching {
                        val robot = Robot()
                        val re = Rectangle(startX.toInt(), startY.toInt(), w.toInt(), h.toInt())
                        callback(robot.createScreenCapture(re))
                    }
                    .onFailure {
                        it.printStackTrace()
                        callback(null)
                    }
                // 主舞台还原
                primaryStage.isIconified = false
            }
        }
    }

    private fun addMouseDraggedEvent(anchorPane: AnchorPane) {
        anchorPane.onMouseDragged = EventHandler { event: MouseEvent ->
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
}

fun Stage.screenShot(callback: (BufferedImage?) -> Unit) =
    ScreenShot().captureScreen(this, callback)
