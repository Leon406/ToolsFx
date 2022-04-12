package me.leon.view

import javafx.geometry.Pos
import javafx.scene.text.Font
import me.leon.REPO_URL
import me.leon.ReleaseInfo
import me.leon.ext.*
import me.leon.ext.fx.Prefs
import me.leon.ext.fx.openInBrowser
import tornadofx.*
import tornadofx.FX.Companion.messages

class UpdateFragment : Fragment(messages["latestVer"]) {
    private val releaseInfo: ReleaseInfo by params
    override val root = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING_2X
        alignment = Pos.CENTER
        text(releaseInfo.info) { font = Font.font(14.0) }
        hbox {
            prefWidth = DEFAULT_SPACING_40X
            prefHeight = DEFAULT_SPACING_20X
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
}
