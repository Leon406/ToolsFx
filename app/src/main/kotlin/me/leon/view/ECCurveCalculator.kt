package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.cast
import me.leon.ext.crypto.*
import tornadofx.*

class ECCurveCalculator : View("ECCurveCalculator") {

    private var isMultiply = false
    @Suppress("VarCouldBeVal") private var eccMethod = "multiply"

    private val radix = 16

    private val selectedCurve = SimpleStringProperty(allCurves.last())
    private val showY2 = SimpleBooleanProperty(false)

    private var tfX1: TextField by singleAssign()
    private var tfY1: TextField by singleAssign()
    private var tfX2: TextField by singleAssign()
    private var tfY2: TextField by singleAssign()
    private var tfX: TextField by singleAssign()
    private var tfY: TextField by singleAssign()
    private var fPoint2: Field by singleAssign()

    override val root = form {
        fieldset {
            field("x1:") { tfX1 = textfield() }
            field("y1:") { tfY1 = textfield() }
        }
        fieldset {
            fPoint2 = field("k:") { tfX2 = textfield() }
            field("y2:") {
                tfY2 = textfield()
                visibleWhen(showY2)
            }
        }

        hbox {
            alignment = Pos.CENTER
            spacing = DEFAULT_SPACING
            togglegroup {
                radiobutton("add")
                radiobutton("subtract")
                radiobutton("multiply") { isSelected = true }
                selectedToggleProperty().addListener { _, _, newValue ->
                    isMultiply =
                        newValue.cast<RadioButton>().text.also { eccMethod = it } == "multiply"
                    fPoint2.text = if (isMultiply) "k:" else "y2:"
                    showY2.value = !isMultiply
                }
            }
            combobox(selectedCurve, allCurves)
            button(messages["run"]) { action { calculate() } }
        }
        fieldset {
            field("x:") { tfX = textfield() }
            field("y:") { tfY = textfield() }
        }
    }

    private fun calculate() {
        runAsync {
            run {
                when (eccMethod) {
                    "add" ->
                        selectedCurve
                            .get()
                            .curveAdd(
                                tfX1.text.also { println(it) }.toBigInteger(radix),
                                tfY1.text.toBigInteger(radix),
                                tfX2.text.toBigInteger(radix),
                                tfY2.text.toBigInteger(radix)
                            )
                    "subtract" ->
                        selectedCurve
                            .get()
                            .curveSubtract(
                                tfX1.text.toBigInteger(radix),
                                tfY1.text.toBigInteger(radix),
                                tfX2.text.toBigInteger(radix),
                                tfY2.text.toBigInteger(radix)
                            )
                    "multiply" ->
                        selectedCurve
                            .get()
                            .curveMultiply(
                                tfX1.text.toBigInteger(radix),
                                tfY1.text.toBigInteger(radix),
                                tfX2.text.toBigInteger(radix)
                            )
                    else -> throw IllegalArgumentException("Unknown method: $eccMethod")
                }
            }
        } ui
            {
                tfX.text = it.first.uppercase()
                tfY.text = it.second.uppercase()
            }
    }
}
