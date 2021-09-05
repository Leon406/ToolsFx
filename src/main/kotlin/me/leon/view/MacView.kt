package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import me.leon.controller.MacController
import me.leon.ext.*
import tornadofx.*

class MacView : View("MAC") {
    private val controller: MacController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val enableIv = SimpleBooleanProperty(false)
    private val enableBits = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var key: TextField
    private lateinit var iv: TextField
    private lateinit var infoLabel: Label
    private lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val keyText: String
        get() = key.text
    private val ivText: String
        get() = iv.text
    private val outputText: String
        get() = output.text
    private var method = "HmacMD5"
    private var outputEncode = "hex"
    private val regAlgReplace =
        "(POLY1305|GOST3411-2012|SIPHASH(?=\\d-)|SIPHASH128|SHA3(?=\\d{3})|DSTU7564|Skein|Threefish)".toRegex()
    private val eventHandler = fileDraggedHandler { input.text = it.first().absolutePath }

    // https://www.bouncycastle.org/specifications.html
    private val algs =
        linkedMapOf(
            "HmacMD5" to listOf("128"),
            "HmacMD4" to listOf("128"),
            "HmacMD2" to listOf("128"),
            "HmacSM3" to listOf("256"),
            "HmacTiger" to listOf("192"),
            "HmacWhirlpool" to listOf("512"),
            "HmacSHA1" to listOf("160"),
            "HmacSHA2" to listOf("224", "256", "384", "512", "512/224", "512/256"),
            "HmacSHA3" to listOf("224", "256", "384", "512"),
            "HmacRIPEMD" to listOf("128", "160", "256", "320"),
            "HmacKeccak" to listOf("224", "256", "288", "384", "512"),
            "HmacDSTU7564" to listOf("256", "384", "512"),
            "SIPHASH" to listOf("2-4", "4-8"),
            "SIPHASH128" to listOf("2-4", "4-8"),
            "HmacDSTU7564" to listOf("256", "384", "512"),
            "HmacSkein" to
                listOf(
                    "256-160",
                    "256-224",
                    "256-256",
                    "512-128",
                    "512-160",
                    "512-224",
                    "512-256",
                    "512-384",
                    "512-512",
                    "1024-384",
                    "1024-512",
                    "1024-1024"
                ),
            "HmacGOST3411" to listOf("256"),
            "HmacGOST3411-2012" to listOf("256", "512"),
            "POLY1305" to
                listOf(
                    "AES",
                    "ARIA",
                    "CAMELLIA",
                    "CAST6",
                    "NOEKEON",
                    "RC6",
                    "SEED",
                    "SERPENT",
                    "SM4",
                    "Twofish"
                ),
            "GMAC" to
                listOf(
                    "AES",
                    "ARIA",
                    "CAMELLIA",
                    "CAST6",
                    "NOEKEON",
                    "RC6",
                    "SEED",
                    "SERPENT",
                    "SM4",
                    "Twofish"
                ),
            "AESCMAC" to listOf("256"),
            "BLOWFISHCMAC" to listOf("256"),
            "DESCMAC" to listOf("256"),
            "DESEDECMAC" to listOf("256"),
            "SEED-CMAC" to listOf("256"),
            "Shacal-2CMAC" to listOf("256"),
            "SM4-CMAC" to listOf("256"),
            "Threefish" to listOf("256CMAC", "512CMAC", "1024CMAC"),
        )
    private val selectedAlgItem = SimpleStringProperty(algs.keys.first())
    private val selectedBits = SimpleStringProperty(algs.values.first().first())
    private lateinit var cbBits: ComboBox<String>
    private val info
        get() = "MAC: $method"
    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label("待处理:")
            button(graphic = imageview(Image("/import.png"))) {
                action { input.text = clipboardText() }
            }
        }
        input =
            textarea() {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            label("算法:  ")
            combobox(selectedAlgItem, algs.keys.toMutableList()) { cellFormat { text = it } }
            label("长度:  ") { paddingAll = DEFAULT_SPACING }
            cbBits =
                combobox(selectedBits, algs.values.first()) {
                    cellFormat { text = it }
                    enableWhen(enableBits)
                }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            label("key: ")
            key = textfield("hmac_key") { promptText = "请输入key" }
            label("iv: ")
            iv =
                textfield {
                    enableWhen(enableIv)
                    promptText = "请输入iv"
                }
            label("输出编码:")
            togglegroup {
                spacing = DEFAULT_SPACING
                radiobutton("hex") { isSelected = true }
                radiobutton("base64")
                selectedToggleProperty().addListener { _, _, new ->
                    outputEncode = (new as RadioButton).text
                }
            }
        }
        selectedAlgItem.addListener { _, _, newValue ->
            newValue?.run {
                cbBits.items = algs[newValue]!!.asObservable()
                selectedBits.set(algs[newValue]!!.first())
                enableBits.value = algs[newValue]!!.size > 1
                enableIv.value = method.contains("POLY1305|-GMAC".toRegex())
            }
        }
        selectedBits.addListener { _, _, newValue ->
            println("selectedBits __ $newValue")
            newValue?.run {
                method =
                    if (selectedAlgItem.get() == "GMAC") "${newValue}-GMAC"
                    else {

                        "${selectedAlgItem.get()}${newValue.takeIf { algs[selectedAlgItem.get()]!!.size > 1 } ?: ""}"
                            .replace("SHA2(?!=\\d{3})".toRegex(), "SHA")
                            .replace(regAlgReplace, "$1-")
                    }
                println("算法 $method")
                if (inputText.isNotEmpty()) {
                    doMac()
                }
            }
        }
        tilepane {
            alignment = Pos.CENTER
            hgap = DEFAULT_SPACING_4X
            button("运行", imageview(Image("/run.png"))) {
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                action { doMac() }
            }
        }
        hbox {
            label("输出内容:")
            button(graphic = imageview(Image("/copy.png"))) { action { outputText.copy() } }
        }
        output =
            textarea {
                promptText = "结果"
                isWrapText = true
            }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { infoLabel = label(info) }
    }

    private fun doMac() =
        runAsync {
            if (method.contains("POLY1305|-GMAC".toRegex()))
                controller.macWithIv(inputText, keyText, ivText, method, outputEncode)
            else controller.mac(inputText, keyText, method, outputEncode)
        } ui
            {
                output.text = it
                infoLabel.text = info
            }
}
