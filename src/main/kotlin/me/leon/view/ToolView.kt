package me.leon.view

import tornadofx.*

class Home : View("开发工具集 (ToolsFx) by Leon406 ") {
    override val root = tabpane {
        tab<EncodeView>()
        tab<DigestView>()
        tab<SymmetricCryptoView>()
        tab<AsymmetricCryptoView>()
    }
}
