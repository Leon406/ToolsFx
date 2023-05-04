package me.leon

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {

    init {
        myButton {
            padding = box(px10)
            fontSize = px20
            fontWeight = FontWeight.BOLD
        }
        group {
            padding = box(8.px)
            spacing = 8.px
            fontSize = 12.px
        }
        center {
            spacing = 8.px
            alignment = Pos.CENTER
        }
        left {
            spacing = 8.px
            alignment = Pos.CENTER_LEFT
        }

        button {
            fontSize = px14
            fontWeight = FontWeight.BOLD
            and(hover) { backgroundColor += hoverColor }
        }

        label {
            fontSize = px14
            padding = box(px5, px10)
            fontWeight = FontWeight.BOLD
            maxWidth = infinity
        }
    }

    companion object {
        // Define our styles
        val myButton by cssclass()
        val group by cssclass()
        val center by cssclass()
        val left by cssclass()

        // Define our colors
        val hoverColor = c("#a1a3a6")
        val dangerColor = c("#a94442")

        // Define our size
        val px14 = 14.px
        val px5 = 5.px
        val px10 = 10.px
        val px20 = 20.px
    }
}
