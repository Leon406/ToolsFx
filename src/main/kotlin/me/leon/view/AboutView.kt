package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.text.Font
import javafx.scene.text.Text
import me.leon.*
import me.leon.ext.*
import tornadofx.*

class AboutView : View("关于") {

    override val closeable = SimpleBooleanProperty(false)
    lateinit var latestVersion: Text

    override val root = vbox {
        alignment = Pos.TOP_CENTER
        spacing = DEFAULT_SPACING
        paddingAll = DEFAULT_SPACING
        imageview(Image("/tb.png"))
        text("版本 v$VERSION") { font = Font.font(18.0) }
        text("构建时间 ${times()}")
        hyperlink("吾爱破解地址") { action { PJ52_URL.openInBrowser() } }
        hyperlink("github开源地址") {
            font = Font.font(18.0)
            action { REPO_URL.openInBrowser() }
        }
        hyperlink("开源协议 ISC") { action { LICENSE.openInBrowser() } }
        button("检测新版本") { action { checkUpdate() } }
        latestVersion = text()
        hyperlink("蓝奏云下载 密码52pj") { action { LAN_ZOU_DOWNLOAD_URL.openInBrowser() } }
        checkUpdate(!Prefs.isIgnoreUpdate)
    }

    private fun checkUpdate(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync { CHECK_UPDATE_URL.readFromNet() } ui
            {
                latestVersion.text =
                    if (it.isEmpty()) "未知错误"
                    else if (VERSION != it)
                        "发现新版本 v$it".also { find<UpdateFragment>().openModal() }
                    else "已是最新版本"
            }
    }
}
