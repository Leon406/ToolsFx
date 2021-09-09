package me.leon.view

import me.leon.VERSION
import me.leon.ext.Prefs
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.FX.Companion.messages
import tornadofx.View
import tornadofx.action
import tornadofx.contextmenu
import tornadofx.get
import tornadofx.item
import tornadofx.menu
import tornadofx.tabpane
import java.security.Security

class Home : View("${messages["appName"]} v.$VERSION") {
    override val root = tabpane {
        tab<EncodeView>()
        tab<EncodeTransferView>()
        tab<StringProcessView>()
        tab<DigestView>()
        tab<MacView>()
        tab<SymmetricCryptoView>()
        tab<SymmetricCryptoStreamView>()
        tab<AsymmetricCryptoView>()
        tab<SignatureView>()
        tab<QrcodeView>()
        tab<AboutView>()
        primaryStage.isAlwaysOnTop = Prefs.alwaysOnTop
        contextmenu {
            item("Top ${"√".takeIf { Prefs.alwaysOnTop } ?: ""}") {
                action {
                    Prefs.alwaysOnTop = !Prefs.alwaysOnTop
                    primaryStage.isAlwaysOnTop = Prefs.alwaysOnTop
                    text = "Top ${"√".takeIf { Prefs.alwaysOnTop } ?: ""}"
                }
            }
            menu("Language") {
                item("English(need restart)${"√".takeIf { Prefs.language != "zh" } ?: ""}") {
                    action { Prefs.language = "en" }
                }
                item("中文(需重启)${"√".takeIf { Prefs.language == "zh" } ?: ""}") {
                    action { Prefs.language = "zh" }
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
