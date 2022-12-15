package me.leon

import me.leon.ext.fx.Prefs
import tornadofx.*

fun main() {
    // only take effect at  prism.allowhidpi=true
    System.setProperty("glass.win.uiScale", ToolsApp.scale)
    System.setProperty(
        "prism.allowhidpi",
        Prefs.hidpi.toString().takeIf { ToolsApp.scale == "-1" } ?: "true"
    )
    launch<ToolsApp>()
}
