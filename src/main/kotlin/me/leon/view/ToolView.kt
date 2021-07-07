package me.leon.view

import tornadofx.*

class Home : View("开发工具集 by Leon406 https://github.com/Leon406/ToolsFx") {
    override val root = tabpane {
        tab<EncodeView>()
        tab<DigestView>()
        tab<SymmetricCryptoView>()
        tab<AsymmetricCryptoView>()
    }
}
