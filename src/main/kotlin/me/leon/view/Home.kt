package me.leon.view

import me.leon.APP_NAME
import me.leon.VERSION
import java.security.Security
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.View
import tornadofx.tabpane

class Home : View("$APP_NAME v.$VERSION") {
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
    }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
