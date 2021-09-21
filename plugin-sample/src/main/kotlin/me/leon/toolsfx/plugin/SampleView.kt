package me.leon.toolsfx.plugin

import tornadofx.*

class SampleView : PluginView("SampleView") {
    override val version = "v1.0.3"
    override val date: String = "2021-09-21"
    override val author = "Leon406"
    override val description = "Sample Plugin"
    override val root = vbox {
        text("date $date")
        text("version $version")
        text("author $author")
        text("description $description")
        text("title $title")

        title = "SampleView"
    }
}
