package me.leon.view

import javafx.geometry.Pos
import me.leon.REPO_URL
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.DEFAULT_SPACING_20X
import me.leon.ext.DEFAULT_SPACING_2X
import me.leon.ext.DEFAULT_SPACING_40X
import me.leon.ext.Prefs
import me.leon.ext.openInBrowser
import tornadofx.FX.Companion.messages
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.get
import tornadofx.hbox
import tornadofx.paddingAll

class UpdateFragment : Fragment(messages["latestVer"]) {
    override val root = hbox {
        prefWidth = DEFAULT_SPACING_40X
        prefHeight = DEFAULT_SPACING_20X
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING_2X
        alignment = Pos.CENTER

        button(messages["upgradeNow"]) { action { REPO_URL.openInBrowser() } }
        button(messages["ignoreOnce"]) {
            action {
                Prefs.isIgnoreUpdate = false
                close()
            }
        }
        button(messages["ignoreForever"]) {
            action {
                Prefs.isIgnoreUpdate = true
                close()
            }
        }
    }
}
