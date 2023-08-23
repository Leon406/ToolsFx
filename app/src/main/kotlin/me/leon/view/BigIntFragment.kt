package me.leon.view

import java.math.BigInteger
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import me.leon.*
import me.leon.controller.CalculatorController
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.Calculator
import me.leon.ext.crypto.calculatorType
import me.leon.ext.fx.copy
import me.leon.ext.math.n2s
import me.leon.ext.math.s2n
import tornadofx.*

class BigIntFragment : Fragment("BigInt") {
    val controller: CalculatorController by inject()

    private var timeConsumption = 0L
    private var startTime = 0L
    private val radix = listOf("2", "8", "10", "16", "36")
    private var selectedAlgo: String = calculatorType.keys.first()

    override val closeable = SimpleBooleanProperty(false)
    private val processing = SimpleBooleanProperty(false)
    private val selectedRadix = SimpleStringProperty("10")

    private var bottomView: Label by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var ta1: TextArea by singleAssign()
    private var ta2: TextArea by singleAssign()
    private var ta3: TextArea by singleAssign()
    private var ta4: TextArea by singleAssign()
    private var ta5: TextArea by singleAssign()
    private var ta6: TextArea by singleAssign()
    @Suppress("TrimMultilineRawString")
    private val bottomInfo
        get() =
            "Func: $selectedAlgo radix: ${selectedRadix.get()} bits: P=${ta1.bits()}  " +
                "Q=${ta2.bits()}  " +
                "N=${ta3.bits()}  " +
                "e=${ta4.bits()}  " +
                "d=${ta5.bits()}  " +
                "C=${ta6.bits()}  " +
                "Output=${
                        runCatching {
                            outputText.lines().first().toBigInteger().bitLength().toString()
                        }.getOrDefault("0")
                    }  " +
                "cost: $timeConsumption ms "

    private var outputText: String
        get() = taOutput.text
        set(value) {
            taOutput.text = value
        }

    override val root = borderpane {
        center = centerLayout()
        bottom = hbox { bottomView = label(bottomInfo) }
    }

    private fun TextArea.bits() =
        "0".takeIf { text.isBlank() }
            ?: text.stripAllSpace().toBigInteger(selectedRadix.get().toInt()).bitLength()

    private fun centerLayout(): VBox {
        return vbox {
            addClass(Styles.group)
            inputLayout(this)
            label("Function:")
            hbox {
                addClass(Styles.group)
                tilepane {
                    vgap = 8.0
                    alignment = Pos.TOP_LEFT
                    prefColumns = 7
                    togglegroup {
                        calculatorType.forEach {
                            radiobutton(it.key) {
                                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                                if (it.value == Calculator.PLUS) isSelected = true
                            }
                        }
                        selectedToggleProperty().addListener { _, _, new ->
                            new.cast<RadioButton>().text.calculatorType()?.also {
                                selectedAlgo = it.algo
                                println(it)
                                timeConsumption = 0
                                bottomView.text = bottomInfo
                                calculate()
                            }
                        }
                    }
                }
            }
            hbox {
                addClass(Styles.group, Styles.center)
                label("radix:")
                combobox(selectedRadix, radix) { cellFormat { text = it } }
                button(messages["run"], imageview(IMG_RUN)) {
                    enableWhen(!processing)
                    action { calculate() }
                }
                button("ECC Calc") { action { find<ECCurveCalculator>().openWindow() } }
            }
            outputLayout(this)
        }
    }

    private fun outputLayout(vBox: VBox) {
        hbox {
                addClass(Styles.left)
                label(messages["output"])
                button("P", graphic = imageview(IMG_UP)) {
                    tooltip(messages["up"])
                    action {
                        if (selectedAlgo == Calculator.FACTOR.algo) {
                            val lines = outputText.lines()
                            ta1.text = lines.first()
                            ta2.text = lines.last()
                        } else {
                            ta1.text = outputText
                        }

                        taOutput.text = ""
                    }
                }
                button("Q", graphic = imageview(IMG_UP)) {
                    tooltip(messages["up"])
                    action {
                        if (selectedAlgo == Calculator.FACTOR.algo) {
                            val lines = outputText.lines()
                            ta1.text = lines.first()
                            ta2.text = lines.last()
                        } else {
                            ta2.text = outputText
                        }
                        taOutput.text = ""
                    }
                }
                button("N", graphic = imageview(IMG_UP)) {
                    tooltip(messages["up"])
                    action {
                        ta3.text = outputText
                        taOutput.text = ""
                    }
                }
                button("e", graphic = imageview(IMG_UP)) {
                    tooltip(messages["up"])
                    action {
                        ta4.text = outputText
                        taOutput.text = ""
                    }
                }
                button("d", graphic = imageview(IMG_UP)) {
                    tooltip(messages["up"])
                    action {
                        ta5.text = outputText
                        taOutput.text = ""
                    }
                }
                button(graphic = imageview(IMG_COPY)) {
                    tooltip(messages["copy"])
                    action { outputText.copy() }
                }
            }
            .also { vBox.add(it) }
        taOutput =
            textarea {
                    vgrow = Priority.ALWAYS
                    promptText = messages["outputHint"]
                    isWrapText = true
                    contextmenu {
                        item("numberToString") { action { number2String() } }
                        item("stringToNumber") { action { string2Number() } }
                        item("numberToBase64") { action { number2Base64() } }
                        item("base64ToNumber") { action { base64ToNumber() } }
                    }
                }
                .also { vBox.add(it) }
    }

    private fun number2String() {
        outputText =
            outputText.lineAction2String { it.toBigInteger(selectedRadix.get().toInt()).n2s() }
    }

    private fun string2Number() {
        outputText = outputText.lineAction2String { it.s2n().toString(selectedRadix.get().toInt()) }
    }

    private fun number2Base64() {
        outputText =
            outputText.lineAction2String {
                it.toBigInteger(selectedRadix.get().toInt()).toByteArray().base64()
            }
    }

    private fun base64ToNumber() {
        outputText =
            outputText.lineAction2String {
                BigInteger(1, it.base64Decode()).toString(selectedRadix.get().toInt())
            }
    }

    private fun inputLayout(vBox: VBox) {
        hbox {
                prefHeight = DEFAULT_SPACING_10X
                alignment = Pos.CENTER_LEFT
                label("P:")
                ta1 = textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_50X
                }
                label("Q:")
                ta2 = textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_50X
                }
            }
            .also { vBox.add(it) }
        hbox {
                prefHeight = DEFAULT_SPACING_10X
                alignment = Pos.CENTER_LEFT
                label("N:")
                ta3 = textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_50X
                }

                label("e:")
                ta4 = textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_50X
                }
            }
            .also { vBox.add(it) }
        hbox {
                prefHeight = DEFAULT_SPACING_10X
                alignment = Pos.CENTER_LEFT
                label("d:")
                ta5 = textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_50X
                }
                label("C:")
                ta6 = textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_50X
                }
            }
            .also { vBox.add(it) }
    }

    private fun calculate() {
        if (ta1.text.isBlank() && ta3.text.isBlank()) {
            return
        }
        runAsync {
            processing.value = true
            startTime = System.currentTimeMillis()
            controller.calculate(
                selectedAlgo,
                selectedRadix.get().toInt(),
                listOf(
                    ta1.text,
                    ta2.text,
                    ta3.text,
                    ta4.text,
                    ta5.text,
                    ta6.text,
                )
            )
        } ui
            {
                processing.value = false
                outputText =
                    runCatching {
                            it.lines().joinToString("\n") {
                                println("$it ${selectedRadix.get().toInt()}")
                                it.toBigInteger().toString(selectedRadix.get().toInt())
                            }
                        }
                        .getOrDefault(it)
                timeConsumption = System.currentTimeMillis() - startTime
                bottomView.text = bottomInfo
            }
    }
}
