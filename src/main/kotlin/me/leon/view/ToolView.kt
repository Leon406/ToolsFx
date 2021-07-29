package me.leon.view

import org.bouncycastle.jce.provider.BouncyCastleProvider
import tornadofx.*
import java.security.Security

class Home : View("开发工具集 (ToolsFx) by Leon406 ") {
    override val root = tabpane {
        tab<EncodeView>()
        tab<DigestView>()
        tab<MacView>()
        tab<SymmetricCryptoView>()
        tab<SymmetricCryptoStreamView>()
        tab<AsymmetricCryptoView>()
    }

    companion object {
        init {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
