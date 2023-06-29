package me.leon.view

import javafx.concurrent.Worker
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.web.WebView
import me.leon.ToolsApp
import me.leon.ext.*
import tornadofx.*

class VocabularyCell : ListCell<Vocabulary>() {
    lateinit var tWord: Label
    lateinit var tMean: Label

    init {
        graphic = hbox {
            tWord = label { this.prefWidth = DEFAULT_SPACING_32X }
            tMean = label()
            padding = Insets(8.0)
            spacing = DEFAULT_SPACING_8X
            setOnMouseClicked {
                if (it.clickCount == 2) {
                    showWordInfo(item.word)
                }
            }
        }
    }

    override fun updateItem(item: Vocabulary?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null) {
            tWord.text = item.word
            tWord.tooltip(item.word)
            tMean.text = item.mean
            tMean.tooltip(item.mean)
        } else {
            tWord.text = ""
            tMean.text = ""
        }
    }

    companion object {
        private val shareWebview =
            WebView().apply {
                engine.userStyleSheetLocation =
                    (OnlineWebView::class.java).getResource("/css/webview.css")?.toExternalForm()
                var startTime = System.currentTimeMillis()

                engine.loadWorker.stateProperty().addListener { _, _, newState ->
                    println("loading $newState")
                    if (newState == Worker.State.READY) {
                        isVisible = false
                        startTime = System.currentTimeMillis()
                    } else if (newState == Worker.State.SUCCEEDED) {
                        val hideJs =
                            if (ToolsApp.dict.hideCssElement.isNotEmpty()) {
                                "hideElements('${ToolsApp.dict.hideCssElement}');"
                            } else {
                                ""
                            }
                        val extraJs =
                            if (ToolsApp.dict.autoPronounce) {
                                ToolsApp.dict.js.orEmpty()
                            } else {
                                ""
                            }
                        runCatching {
                                engine.executeScript(
                                    "function hideElements(selector){" +
                                        "var items=document.querySelectorAll(selector);" +
                                        "for(var i=0,size=items.length;i<size;i++){" +
                                        "items[i].style.display='none'}}\n$hideJs;$extraJs"
                                )
                            }
                            .getOrElse { println(it.stacktrace()) }
                        println("it takes ${System.currentTimeMillis() - startTime}")
                        isVisible = true
                    }
                }
            }

        fun showWordInfo(word: String) {
            Alert(Alert.AlertType.WARNING)
                .apply {
                    title = "${ToolsApp.dict.name} $word"
                    headerText = ""
                    dialogPane.prefWidth = 800.0
                    graphic =
                        shareWebview.attachTo(this) {
                            engine.load(ToolsApp.dict.url.format(word).also { println(it) })
                        }
                }
                .show()
        }
    }
}

data class Vocabulary(val word: String, val mean: String? = null) {

    override fun equals(other: Any?): Boolean {
        if (other !is Vocabulary) {
            return false
        }
        return word == other.word
    }

    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + mean.hashCode()
        return result
    }
}
