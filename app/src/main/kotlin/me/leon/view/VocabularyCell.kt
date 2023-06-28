package me.leon.view

import javafx.concurrent.Worker
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ListCell
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import javafx.scene.web.WebView
import me.leon.ToolsApp
import me.leon.ext.DEFAULT_SPACING_40X
import me.leon.ext.stacktrace
import tornadofx.*

class VocabularyCell : ListCell<Vocabulary>() {
    private val tWord = Text()

    init {

        val container = HBox(tWord)
        container.prefWidth = DEFAULT_SPACING_40X
        container.padding = Insets(8.0)
        graphic = container

        container.setOnMouseClicked { event ->
            // 双击事件
            if (event.clickCount == 2) {
                showWordInfo(item.word)
            }
        }
    }

    override fun updateItem(item: Vocabulary?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null) {
            tWord.text = item.word
        } else {
            tWord.text = ""
        }
    }

    companion object {
        private val shareWebview =
            WebView().apply {
                engine.userStyleSheetLocation =
                    (OnlineWebView::class.java).getResource("/css/webview.css")?.toExternalForm()
                engine.loadWorker.stateProperty().addListener { _, _, newState ->
                    println("loading $newState")
                    if (newState == Worker.State.SUCCEEDED) {
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

data class Vocabulary(val word: String, val explanation: String = "") {

    override fun equals(other: Any?): Boolean {
        if (other !is Vocabulary) {
            return false
        }
        return word == other.word
    }

    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + explanation.hashCode()
        return result
    }
}
