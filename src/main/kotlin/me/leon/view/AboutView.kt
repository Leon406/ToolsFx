package me.leon.view

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.text.Font
import me.leon.CHECK_UPDATE_URL
import me.leon.LAN_ZOU_DOWNLOAD_URL
import me.leon.REPO_URL
import me.leon.VERSION
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.openInBrowser
import me.leon.ext.readFromNet
import tornadofx.*

class AboutView : View("关于") {
    override val root = vbox {
        alignment = Pos.TOP_CENTER
        spacing = DEFAULT_SPACING
        paddingAll = DEFAULT_SPACING
        imageview(Image("/tb.png"))
        text("版本 v$VERSION") { font = Font.font(18.0) }
        hyperlink("github地址") { action { REPO_URL.openInBrowser() } }
        hyperlink("蓝奏云下载 密码52pj") { action { LAN_ZOU_DOWNLOAD_URL.openInBrowser() } }
        button("检测新版本") {
            action {
                CHECK_UPDATE_URL.readFromNet()
                find<MyFragment>().openModal()
            }
        }
    }
}
