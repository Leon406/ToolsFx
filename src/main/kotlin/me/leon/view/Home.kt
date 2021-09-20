package me.leon.view

import java.security.Security
import me.leon.ToolsApp.Companion.isEnableClassical
import me.leon.VERSION
import me.leon.ext.Prefs
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import tornadofx.FX.Companion.messages

class Home : View("${messages["appName"]} v.$VERSION") {
    override val root = tabpane {
        if (isEnableClassical) {
            tab<ClassicalView>()
        }
        tab<EncodeView>()
        tab<OnlineWebView>()
        tab<EncodeTransferView>()
        tab<StringProcessView>()
        tab<DigestView>()
        tab<MacView>()
        tab<SymmetricCryptoView>()
        tab<SymmetricCryptoStreamView>()
        tab<AsymmetricCryptoView>()
        tab<SignatureView>()
        tab<QrcodeView>()
        tab<PBEView>()
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
