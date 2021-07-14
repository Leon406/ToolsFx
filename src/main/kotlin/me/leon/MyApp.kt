package me.leon

import javafx.scene.image.Image
import javafx.stage.Stage
import me.leon.view.Home
import tornadofx.*

class MyApp: App(Home::class, Styles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.icons.add(Image(resources.stream("/tb.png")))
    }
}