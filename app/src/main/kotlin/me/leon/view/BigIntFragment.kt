package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.VBox
import me.leon.*
import me.leon.controller.CalculatorController
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.Calculator
import me.leon.ext.crypto.calculatorType
import me.leon.ext.fx.copy
import tornadofx.*

class BigIntFragment : Fragment("BigInt") {
    val controller: CalculatorController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private lateinit var bottomView: Label
    private var timeConsumption = 0L
    private var startTime = 0L
    private val bottomInfo
        get() =
            "Func: $selectedAlgo radix: ${selectedRadix.get()} bits: P=${ta1.bits()}  " +
                "Q=${ta2.bits()}  " +
                "N=${ta3.bits()}  " +
                "a=${ta4.bits()}  " +
                "b=${ta5.bits()}  " +
                "Output=${
                        runCatching {
                            outputText.lineSplit().first().toBigInteger().bitLength().toString()
                        }.getOrDefault("0")
                    }  " +
                "cost: $timeConsumption ms "
    private var outputText: String
        get() = taOutput.text
        set(value) {
            taOutput.text = value
        }

    private fun TextArea.bits() =
        "0".takeIf { text.isBlank() } ?: text.toBigInteger(selectedRadix.get().toInt()).bitLength()

    private val radix = listOf("2", "8", "10", "16", "36")
    private val selectedRadix = SimpleStringProperty("10")

    lateinit var taOutput: TextArea
    lateinit var ta1: TextArea
    lateinit var ta2: TextArea
    lateinit var ta3: TextArea
    lateinit var ta4: TextArea
    lateinit var ta5: TextArea
    lateinit var ta6: TextArea
    private var selectedAlgo: String = calculatorType.keys.first()
    override val root = borderpane {
        center = centerLayout()
        bottom = hbox { bottomView = label(bottomInfo) }
    }

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
                button(messages["run"], imageview("/img/run.png")) {
                    enableWhen(!isProcessing)
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
            button("P", graphic = imageview("/img/up.png")) {
                action {
                    ta1.text = outputText
                    taOutput.text = ""
                }
            }
            button("Q", graphic = imageview("/img/up.png")) {
                action {
                    ta2.text = outputText
                    taOutput.text = ""
                }
            }
            button("N", graphic = imageview("/img/up.png")) {
                action {
                    ta3.text = outputText
                    taOutput.text = ""
                }
            }
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
        }
            .also { vBox.add(it) }
        taOutput =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
                contextmenu {
                    item("numberToString") {
                        action {
                            outputText =
                                outputText.lineAction2String {
                                    it.toBigInteger(selectedRadix.get().toInt()).n2s()
                                }
                        }
                    }
                    item("stringToNumber") {
                        action {
                            outputText =
                                outputText.lineAction2String {
                                    it.s2n().toString(selectedRadix.get().toInt())
                                }
                        }
                    }
                    item("numberToBase64") {
                        action {
                            outputText =
                                outputText.lineAction2String {
                                    it.toBigInteger(selectedRadix.get().toInt())
                                        .toByteArray()
                                        .base64()
                                }
                        }
                    }
                    item("base64ToNumber") {
                        action {
                            outputText =
                                outputText.lineAction2String {
                                    it.base64Decode()
                                        .toBigInteger()
                                        .toString(selectedRadix.get().toInt())
                                }
                        }
                    }
                }
            }
                .also { vBox.add(it) }
    }

    private fun inputLayout(vBox: VBox) {
        hbox {
            prefHeight = DEFAULT_SPACING_10X
            alignment = Pos.CENTER_LEFT
            label("P:")
            ta1 =
                textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_40X
                }
            label("Q:")
            ta2 =
                textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_40X
                }
        }
            .also { vBox.add(it) }
        hbox {
            prefHeight = DEFAULT_SPACING_10X
            alignment = Pos.CENTER_LEFT
            label("N:")
            ta3 =
                textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_40X
                }

            label("a:")
            ta4 =
                textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_40X
                }
        }
            .also { vBox.add(it) }
        hbox {
            prefHeight = DEFAULT_SPACING_10X
            alignment = Pos.CENTER_LEFT
            label("b:")
            ta5 =
                textarea {
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_40X
                }
            label("C:")
            ta6 =
                textarea {
                    enableWhen(SimpleBooleanProperty(false))
                    isWrapText = true
                    prefWidth = DEFAULT_SPACING_40X
                }
        }
            .also { vBox.add(it) }
    }

    private fun calculate() {
        if (ta1.text.isBlank()) {
            return
        }
        runAsync {
            isProcessing.value = true
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
                isProcessing.value = false
                outputText =
                    runCatching {
                            it.lineSplit().joinToString("\n") {
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
