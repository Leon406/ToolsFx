package me.leon.view

import java.security.Security
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.View
import tornadofx.tabpane

class Home : View("开发工具集 (ToolsFx) by Leon406 ") {
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
