package me.leon.view

import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.Worker
import javafx.scene.control.TextField
import javafx.scene.web.WebView
import me.leon.Styles
import me.leon.ToolsApp
import me.leon.ext.DEFAULT_SPACING_40X
import me.leon.ext.fx.openInBrowser
import tornadofx.*

class OnlineWebView : Fragment("Browser") {
    private var web: WebView by singleAssign()
    private var tfUrl: TextField by singleAssign()
    private val selectedUrl = SimpleStringProperty(ToolsApp.extUrls.first())
    private val fontJS by lazy {
        javaClass.getResourceAsStream("/js/font.js")?.readBytes()?.decodeToString()
    }

    override val root = borderpane {
        top =
            hbox {
                addClass(Styles.group, Styles.left)
                button(graphic = imageview("img/back.png")) {
                    action {
                        web.engine.history.run {
                            println("currentIndex $currentIndex  $entries.size")
                            if (currentIndex > 0) go(-1)
                        }
                    }
                }
                button(graphic = imageview("/img/forward.png")) {
                    action {
                        web.engine.history.run {
                            println("forward currentIndex $currentIndex  ${entries.size}")
                            if (currentIndex < entries.size - 1) go(1)
                        }
                    }
                }
                button(graphic = imageview("/img/refresh.png")) { action { web.engine.reload() } }
                tfUrl =
                    textfield(selectedUrl.get()) {
                        promptText = "input url"
                        prefWidth = DEFAULT_SPACING_40X
                        setOnAction { web.engine.load(tfUrl.text) }
                    }
                combobox(selectedUrl, ToolsApp.extUrls.toMutableList()) { cellFormat { text = it } }
                selectedUrl.addListener { _, _, newValue ->
                    println("selectedUrl $newValue")
                    tfUrl.text = newValue as String
                    println("selectedUrl2 ${tfUrl.text}")
                    web.engine.load(tfUrl.text)
                }
                button(graphic = imageview("/img/run.png")) {
                    action { web.engine.load(tfUrl.text.ifEmpty { selectedUrl.get() }) }
                }
                button(graphic = imageview("/img/browser.png")) {
                    action { tfUrl.text.openInBrowser() }
                }
            }
        center =
            vbox {
                web =
                    webview {
                        engine.load(ToolsApp.extUrls.first())
                        engine.loadWorker.stateProperty().addListener { _, _, newState ->
                            println("loading $newState")

                            if (newState == Worker.State.SUCCEEDED) {
                                println(
                                    "load ${engine.history.entries[engine.history.currentIndex].url}"
                                )
                                tfUrl.text = engine.history.entries[engine.history.currentIndex].url
                                web.engine.executeScript(fontJS).also { println(it) }
                            }
                        }
                    }
            }

        bottom =
            hbox {
                addClass(Styles.group, Styles.left)
                val tf =
                    textfield("document.body.style.fontFamily=\"SimSun\"") {
                        prefWidth = DEFAULT_SPACING_40X
                    }
                button("inject js") { action { web.engine.executeScript(tf.text) } }
            }
    }
}
