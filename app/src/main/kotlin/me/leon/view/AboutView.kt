package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.text.Font
import javafx.scene.text.Text
import me.leon.*
import me.leon.ext.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class AboutView : View(messages["about"]) {

    override val closeable = SimpleBooleanProperty(false)
    private lateinit var txtLatestVersion: Text
    override val root = vbox {
        alignment = Pos.CENTER
        spacing = DEFAULT_SPACING
        paddingAll = DEFAULT_SPACING
        imageview("/img/tb.png")
        text("${messages["ver"]}: v$VERSION") { font = Font.font(18.0) }
        text("Build: ${times()}")
        text("JRE: ${System.getProperty("java.runtime.version")}")
        text("VM: ${System.getProperty("java.vm.name")}")
        hyperlink("吾爱破解地址") { action { PJ52_URL.openInBrowser() } }
        hyperlink("github开源地址") {
            font = Font.font(18.0)
            action { REPO_URL.openInBrowser() }
        }
        hyperlink(messages["license"]) { action { LICENSE.openInBrowser() } }
        button(messages["checkUpdate"]) {
            action {
                Prefs.isIgnoreUpdate = false
                checkUpdate()
            }
        }
        button(messages["checkUpdateDev"]) {
            action {
                Prefs.isIgnoreUpdate = false
                checkUpdateDev()
            }
        }
        txtLatestVersion = text()
        hyperlink("蓝奏云下载 密码52pj") { action { LAN_ZOU_DOWNLOAD_URL.openInBrowser() } }
        if (VERSION.endsWith("beta")) checkUpdateDev(!Prefs.isIgnoreUpdate)
        else checkUpdate(!Prefs.isIgnoreUpdate)
    }

    private fun checkUpdateDev(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync { DEV_UPDATE_URL.readFromNet(DEV_UPDATE_URL2) } ui
                {
                    txtLatestVersion.text =
                        if (it.isEmpty()) messages["unknown"]
                        else if (VERSION != it)
                            "${messages["latestVer"]} v$it".also { find<UpdateFragment>().openModal() }
                        else messages["alreadyLatest"]
                }
    }

    private fun checkUpdate(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync { CHECK_UPDATE_URL.readFromNet(CHECK_UPDATE_URL2) } ui
                {
                    txtLatestVersion.text =
                        if (it.isEmpty()) messages["unknown"]
                        else if (!VERSION.contains("beta") && VERSION != it)
                            "${messages["latestVer"]} v$it".also { find<UpdateFragment>().openModal() }
                        else messages["alreadyLatest"]
                }
    }
}
