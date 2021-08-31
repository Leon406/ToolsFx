package me.leon

import javafx.scene.image.Image
import me.leon.view.Home
import tornadofx.App
import tornadofx.addStageIcon

class MyApp : App(Home::class, Styles::class) {
    init {
        addStageIcon(Image(resources.stream("/tb.png")))
    }
}
