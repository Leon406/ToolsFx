package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.text.Font
import javafx.scene.text.Text
import me.leon.*
import me.leon.config.*
import me.leon.domain.ReleaseInfo
import me.leon.ext.*
import me.leon.ext.fx.Prefs
import me.leon.ext.fx.openInBrowser
import tornadofx.*
import tornadofx.FX.Companion.messages

class AboutView : Fragment(messages["about"]) {

    override val closeable = SimpleBooleanProperty(false)
    private val fetching = SimpleBooleanProperty(false)

    private var txtLatestVersion: Text by singleAssign()
    private lateinit var releaseInfo: ReleaseInfo

    override val root = vbox {
        alignment = Pos.CENTER
        spacing = DEFAULT_SPACING
        paddingAll = DEFAULT_SPACING
        imageview(IMG_ICON)
        // 避免字符串模板编译优化成常量,导致无法动态修改
        text("${messages["ver"]}: v$appVersion") { font = Font.font(18.0) }
        text("Build: $build")
        text("JRE: ${System.getProperty("java.runtime.version")}")
        text("VM: ${System.getProperty("java.vm.name")}")

        hyperlink("wiki").action { WIKI.openInBrowser() }
        hyperlink("吾爱破解地址").action { PJ52_URL.openInBrowser() }
        hyperlink(messages["feedback"]).action { REPO_ISSUE.openInBrowser() }
        hyperlink(messages["license"]).action { LICENSE.openInBrowser() }

        button(messages["checkUpdate"]) {
            enableWhen(!fetching)
            action {
                Prefs.isIgnoreUpdate = false
                checkUpdate()
            }
        }
        button(messages["checkUpdateDev"]) {
            enableWhen(!fetching)
            action {
                Prefs.isIgnoreUpdate = false
                checkUpdateDev()
            }
        }
        txtLatestVersion = text()
        hyperlink("蓝奏云下载 密码52pj").action { LAN_ZOU_DOWNLOAD_URL.openInBrowser() }
        hyperlink("插件下载 密码ax63").action { LAN_ZOU_PLUGIN_DOWNLOAD_URL.openInBrowser() }
        if (appVersion.contains("beta")) {
            checkUpdateDev(!Prefs.isIgnoreUpdate)
        } else {
            checkUpdate(!Prefs.isIgnoreUpdate)
        }
    }

    private fun checkUpdateDev(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync {
            fetching.value = true
            DEV_UPDATE_URL.readFromNet(DEV_UPDATE_URL2)
        } ui
            {
                fetching.value = false
                releaseInfo = it.fromJson(ReleaseInfo::class.java)
                txtLatestVersion.text =
                    if (it.isEmpty()) {
                        messages["unknown"]
                    } else if (appVersion != releaseInfo.version) {
                        "${messages["latestVer"]} v${releaseInfo.version}"
                            .also {
                                find<UpdateFragment>(mapOf("releaseInfo" to releaseInfo))
                                    .openModal()
                            }
                    } else {
                        messages["alreadyLatest"]
                    }
            }
    }

    private fun checkUpdate(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync {
            fetching.value = true
            CHECK_UPDATE_URL.readFromNet(CHECK_UPDATE_URL2)
        } ui
            {
                fetching.value = false
                releaseInfo = it.fromJson(ReleaseInfo::class.java)
                txtLatestVersion.text =
                    if (it.isEmpty()) {
                        messages["unknown"]
                    } else if (!appVersion.contains("beta") && appVersion != releaseInfo.version) {
                        "${messages["latestVer"]} v${releaseInfo.version}"
                            .also {
                                find<UpdateFragment>(mapOf("releaseInfo" to releaseInfo))
                                    .openModal()
                            }
                    } else {
                        messages["alreadyLatest"]
                    }
            }
    }
}
