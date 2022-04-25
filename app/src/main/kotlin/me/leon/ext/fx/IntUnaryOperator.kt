package me.leon.ext.fx

import java.util.function.UnaryOperator
import javafx.scene.control.TextFormatter

class IntUnaryOperator : UnaryOperator<TextFormatter.Change?> {
    override fun apply(change: TextFormatter.Change?): TextFormatter.Change? {
        return change?.let { if (it.text.matches("\\d*".toRegex())) it else null }
    }
}

val intTextFormatter
    get() = TextFormatter<Int>(IntUnaryOperator())
