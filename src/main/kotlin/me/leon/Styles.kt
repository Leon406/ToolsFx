package me.leon

import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        // Define our styles
        val myButton by cssclass()

        // Define our colors
        val hoverColor = c("#395bae")
        val dangerColor = c("#a94442")
    }

    init {

        myButton {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
        button {
            fontSize = 14.px
            fontWeight = FontWeight.BOLD
//            padding = box(10.px)
         and(hover) {
                backgroundColor += hoverColor
//             borderColor += box(dangerColor)
            }
        }

        label {
            fontSize = 14.px
            padding = box(5.px, 10.px)
            fontWeight = FontWeight.BOLD
            maxWidth = infinity
//            borderColor += box(dangerColor)
//            borderStyle += BorderStrokeStyle(
//                StrokeType.INSIDE,
//                StrokeLineJoin.MITER,
//                StrokeLineCap.BUTT,
//                10.0,
//                0.0,
//                listOf(25.0, 5.0)
//            )
//            borderWidth += box(5.px)
//
//            and(hover) {
//                backgroundColor += hoverColor
//                borderColor += box(dangerColor)
//            }

        }


    }
}
