package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.text.Font
import javafx.scene.text.Text
import me.leon.*
import me.leon.ext.*
import me.leon.ext.fx.Prefs
import me.leon.ext.fx.openInBrowser
import tornadofx.*
import tornadofx.FX.Companion.messages

class AboutView : Fragment(messages["about"]) {

    override val closeable = SimpleBooleanProperty(false)
    private var txtLatestVersion: Text by singleAssign()
    private lateinit var releaseInfo: ReleaseInfo
    private val isFetching = SimpleBooleanProperty(false)

    override val root = vbox {
        alignment = Pos.CENTER
        spacing = DEFAULT_SPACING
        paddingAll = DEFAULT_SPACING
        imageview("/img/tb.png")
        text("${messages["ver"]}: v$VERSION") { font = Font.font(18.0) }
        text("Build: $BUILD_DATE")
        text("JRE: ${System.getProperty("java.runtime.version")}")
        text("VM: ${System.getProperty("java.vm.name")}")
        hyperlink("feedback").action { REPO_ISSUE.openInBrowser() }
        hyperlink(messages["license"]).action { LICENSE.openInBrowser() }
        button(messages["checkUpdate"]) {
            enableWhen(!isFetching)
            action {
                Prefs.isIgnoreUpdate = false
                checkUpdate()
            }
        }
        button(messages["checkUpdateDev"]) {
            enableWhen(!isFetching)
            action {
                Prefs.isIgnoreUpdate = false
                checkUpdateDev()
            }
        }
        txtLatestVersion = text()
        hyperlink("蓝奏云下载 密码52pj").action { LAN_ZOU_DOWNLOAD_URL.openInBrowser() }
        hyperlink("插件下载 密码ax63").action { LAN_ZOU_PLUGIN_DOWNLOAD_URL.openInBrowser() }
        if (VERSION.contains("beta")) checkUpdateDev(!Prefs.isIgnoreUpdate)
        else checkUpdate(!Prefs.isIgnoreUpdate)
    }

    private fun checkUpdateDev(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync {
            isFetching.value = true
            DEV_UPDATE_URL.readFromNet(DEV_UPDATE_URL2)
        } ui
            {
                isFetching.value = false
                releaseInfo = it.fromJson(ReleaseInfo::class.java)
                txtLatestVersion.text =
                    if (it.isEmpty()) messages["unknown"]
                    else if (VERSION != releaseInfo.version)
                        "${messages["latestVer"]} v${releaseInfo.version}".also {
                            find<UpdateFragment>(mapOf("releaseInfo" to releaseInfo)).openModal()
                        }
                    else messages["alreadyLatest"]
            }
    }

    private fun checkUpdate(isAuto: Boolean = true) {
        if (!isAuto) return
        runAsync {
            isFetching.value = true
            CHECK_UPDATE_URL.readFromNet(CHECK_UPDATE_URL2)
        } ui
            {
                isFetching.value = false
                releaseInfo = it.fromJson(ReleaseInfo::class.java)
                txtLatestVersion.text =
                    if (it.isEmpty()) messages["unknown"]
                    else if (!VERSION.contains("beta") && VERSION != releaseInfo.version)
                        "${messages["latestVer"]} v${releaseInfo.version}".also {
                            find<UpdateFragment>(mapOf("releaseInfo" to releaseInfo)).openModal()
                        }
                    else messages["alreadyLatest"]
            }
    }
}
