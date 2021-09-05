package me.leon

import java.util.*
import javafx.scene.image.Image
import me.leon.view.Home
import tornadofx.*

class MyApp : App(Home::class, Styles::class) {
    init {
        FX.locale = Locale.CHINESE
        addStageIcon(Image(resources.stream("/tb.png")))
    }
}
