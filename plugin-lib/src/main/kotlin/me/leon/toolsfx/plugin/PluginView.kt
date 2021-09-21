package me.leon.toolsfx.plugin

import javafx.scene.Node
import tornadofx.*

abstract class PluginView(title: String? = null, icon: Node? = null) : View(title, icon) {
    abstract val version: String
    abstract val date: String
    abstract val author: String
    abstract val description: String
}
