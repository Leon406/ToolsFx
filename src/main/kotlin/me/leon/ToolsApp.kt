package me.leon

import java.util.*
import javafx.scene.image.Image
import me.leon.ext.Prefs
import me.leon.view.Home
import tornadofx.*

class ToolsApp : App(Home::class, Styles::class) {
    init {
        // for text i18n
        FX.locale = if (Prefs.language == "zh") Locale.CHINESE else Locale.ENGLISH
        addStageIcon(Image(resources.stream("/img/tb.png")))
    }
}
