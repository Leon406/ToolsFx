package me.leon.view

import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.Worker
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.web.WebView
import me.leon.ToolsApp
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.DEFAULT_SPACING_40X
import tornadofx.*

class OnlineWebView : View("Browser") {
    private lateinit var web: WebView
    private lateinit var tfUrl: TextField

    private val selectedUrl = SimpleStringProperty(ToolsApp.extUrls.first())
    override val root = borderpane {
        top =
            hbox {
                spacing = DEFAULT_SPACING
                paddingAll = DEFAULT_SPACING
                alignment = Pos.BASELINE_LEFT
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

//                button("inject") {
//                    action { web.engine.executeScript("\$('.navbar-header').hide()") }
//                }
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
                            }
                        }
                    }
            }
    }
}
