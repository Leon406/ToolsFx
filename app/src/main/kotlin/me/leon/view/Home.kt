package me.leon.view

import java.security.Security
import me.leon.ToolsApp.Companion.isEnableClassical
import me.leon.ToolsApp.Companion.isEnableInternalWebview
import me.leon.ToolsApp.Companion.isEnableMac
import me.leon.ToolsApp.Companion.isEnablePBE
import me.leon.ToolsApp.Companion.isEnableQrcode
import me.leon.ToolsApp.Companion.isEnableSignature
import me.leon.ToolsApp.Companion.isEnableSymmetricStream
import me.leon.ToolsApp.Companion.plugins
import me.leon.VERSION
import me.leon.ext.Prefs
import me.leon.toolsfx.plugin.PluginView
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import tornadofx.FX.Companion.messages
import java.util.ServiceLoader

class Home : View("${messages["appName"]} v.$VERSION") {
    override val root = tabpane {
        if (isEnableClassical) tab<ClassicalView>()

        tab<EncodeView>()
        tab<EncodeTransferView>()
        tab<StringProcessView>()
        tab<DigestView>()
        if (isEnableMac) tab<MacView>()
        tab<SymmetricCryptoView>()
        if (isEnableSymmetricStream) tab<SymmetricCryptoStreamView>()
        tab<AsymmetricCryptoView>()
        if (isEnableSignature) tab<SignatureView>()
        if (isEnableQrcode) tab<QrcodeView>()
        if (isEnablePBE) tab<PBEView>()
        if (isEnableInternalWebview)
            runCatching { Class.forName("javafx.scene.web.WebView") }.onSuccess {
                tab<OnlineWebView>()
            }

        plugins.forEach {
            with(it.newInstance() as PluginView) {
                tab(this) {
                    this.text = this@with.description
                    println(this.text)
                }
            }
        }

        // support library
        val sl: ServiceLoader<PluginView> = ServiceLoader.load(PluginView::class.java)
        println(sl.findFirst())
        sl.forEach {
            tab(it) {
                this.text = it.description
                println(this.text)
            }
        }
        tab<AboutView>()
        primaryStage.isAlwaysOnTop = Prefs.alwaysOnTop
        contextmenu {
            item("Top ${"  √".takeIf { Prefs.alwaysOnTop } ?: ""}") {
                action {
                    Prefs.alwaysOnTop = !Prefs.alwaysOnTop
                    primaryStage.isAlwaysOnTop = Prefs.alwaysOnTop
                    text = "Top ${"  √".takeIf { Prefs.alwaysOnTop } ?: ""}"
                }
            }
            menu("Language") {
                item("English(need restart)${"  √".takeIf { Prefs.language != "zh" } ?: ""}") {
                    action { Prefs.language = "en" }
                }
                item("中文(需重启)${"  √".takeIf { Prefs.language == "zh" } ?: ""}") {
                    action { Prefs.language = "zh" }
                }
            }

            item("${messages["autoCopy"]}${"  √".takeIf { Prefs.autoCopy } ?: ""}") {
                action {
                    Prefs.autoCopy = !Prefs.autoCopy
                    text = "${messages["autoCopy"]}${"  √".takeIf { Prefs.autoCopy } ?: ""}"
                }
            }
        }
    }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
