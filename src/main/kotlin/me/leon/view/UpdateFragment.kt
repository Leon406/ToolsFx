package me.leon.view

import javafx.geometry.Pos
import me.leon.REPO_URL
import me.leon.ext.*
import tornadofx.*

class UpdateFragment : Fragment("检测到新版本") {
    override val root = hbox {
        prefWidth = DEFAULT_SPACING_40X
        prefHeight = DEFAULT_SPACING_20X
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING_2X
        alignment = Pos.CENTER

        button("马上升级") { action { action { REPO_URL.openInBrowser() } } }
        button("忽略本次") { action { close() } }
        button("永久忽略") {
            action {
                Prefs.isIgnoreUpdate = true
                close()
            }
        }
    }
}
