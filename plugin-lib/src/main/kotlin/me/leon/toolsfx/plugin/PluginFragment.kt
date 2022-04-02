package me.leon.toolsfx.plugin

import javafx.scene.Node
import tornadofx.*

abstract class PluginFragment(title: String? = null, icon: Node? = null) : Fragment(title, icon) {
    abstract val version: String
    abstract val date: String
    abstract val author: String
    abstract val description: String
}
