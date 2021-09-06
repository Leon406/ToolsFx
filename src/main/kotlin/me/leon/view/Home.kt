package me.leon.view

import java.security.Security
import me.leon.VERSION
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import tornadofx.FX.Companion.messages

class Home : View("${messages["appName"]} v.$VERSION") {
    override val root = tabpane {
        tab<EncodeView>()
        tab<EncodeTransferView>()
        tab<DigestView>()
        tab<MacView>()
        tab<SymmetricCryptoView>()
        tab<SymmetricCryptoStreamView>()
        tab<AsymmetricCryptoView>()
        tab<SignatureView>()
        tab<QrcodeView>()
        tab<AboutView>()
        primaryStage.isAlwaysOnTop = true
    }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
