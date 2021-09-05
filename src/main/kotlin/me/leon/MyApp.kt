package me.leon

import javafx.scene.image.Image
import me.leon.view.Home
import tornadofx.*

class MyApp : App(Home::class, Styles::class) {
    init {
        // for text i18n
        // FX.locale = Locale.ENGLISH
        addStageIcon(Image(resources.stream("/tb.png")))
    }
}
