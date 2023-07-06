package me.leon.component

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javafx.application.Platform
import javafx.stage.Stage
import javax.swing.*
import me.leon.IMG_ICON

/**
 * @author Leon
 * @since 2023-04-04 9:25
 * @email deadogone@gmail.com
 */
object Tray {
    private var trayIcon: TrayIcon? = null
    private var popWindow: JDialog? = null

    /** 系统托盘 */
    fun systemTray(stage: Stage) {
        try {
            if (trayIcon == null) {
                Platform.setImplicitExit(false)
                popWindow = popWindow()
                val trayMenu =
                    object : JPopupMenu() {
                        override fun firePopupMenuWillBecomeInvisible() {
                            Platform.runLater {
                                println("~~~~trayMenu")
                                popWindow = null
                            }
                        }
                    }
                val show = JMenuItem("Show:显示") // "Show:显示"
                show.addActionListener {
                    Platform.runLater {
                        println("~~~~show")
                        show(stage)
                    }
                }
                val exit = JMenuItem("Exit:退出") // "Exit:退出"
                exit.addActionListener { System.exit(0) }
                val exitTray = JMenuItem("ExitTray:退出托盘") // "ExitTray:退出托盘"
                exitTray.addActionListener {
                    Platform.runLater {
                        println("~~~~exitTray")
                        stage.show()
                        removeTray()
                    }
                }
                trayMenu.add(show)
                trayMenu.add(exitTray)
                trayMenu.add(exit)
                // 加载系统托盘组件
                trayIcon =
                    TrayIcon(
                            Toolkit.getDefaultToolkit()
                                .getImage(Tray.javaClass.getResource(IMG_ICON)),
                            "ToolsFx"
                        )
                        .apply {
                            isImageAutoSize = true
                            addMouseListener(
                                object : MouseAdapter() {
                                    override fun mouseReleased(e: MouseEvent) {
                                        // 左键点击
                                        if (e.button == 1) {
                                            Platform.runLater { stage.show() }
                                        } else if (e.button == 3 && e.isPopupTrigger) {
                                            // 右键点击弹出JPopupMenu绑定的载体以及JPopupMenu
                                            popWindow = popWindow()
                                            popWindow!!.setLocation(e.x + 5, e.y - 5 - 30)
                                            popWindow!!.isVisible = true
                                            trayMenu.show(popWindow, 0, 0)
                                        }
                                    }
                                }
                            )
                        }
                SystemTray.getSystemTray().add(trayIcon)
            }
            stage.hide()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    private fun popWindow(): JDialog {
        if (popWindow != null) {
            return popWindow!!
        }
        return JDialog().apply {
            isUndecorated = true
            setSize(0, 0)
        }
    }

    private fun show(stage: Stage) {
        if (stage.isIconified) {
            stage.isIconified = false
        }
        if (!stage.isShowing) {
            stage.show()
        }
        stage.toFront()
    }

    fun removeTray() {
        Platform.setImplicitExit(true)
        SystemTray.getSystemTray().remove(trayIcon)
        trayIcon = null
    }
}
