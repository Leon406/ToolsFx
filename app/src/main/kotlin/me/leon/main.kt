package me.leon

import me.leon.ext.fx.Prefs
import tornadofx.*

fun main() {
    System.setProperty("prism.allowhidpi", Prefs.hidpi.toString())
    launch<ToolsApp>()
}
