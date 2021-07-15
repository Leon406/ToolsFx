package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.DragEvent
import me.leon.ext.copy
import tornadofx.*

class MacView : View("MAC") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var key: TextField
    private lateinit var infoLabel: Label
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val keyText: String
        get() = key.text
    private val outputText: String
        get() = output.text
    var method = "HmacMD5"
    var outputEncode = "hex"

    private val eventHandler =
        EventHandler<DragEvent> {
            println("${it.dragboard.hasFiles()}______" + it.eventType)
            if (it.eventType.name == "DRAG_ENTERED") {
                if (it.dragboard.hasFiles()) {
                    println(it.dragboard.files)
                    input.text = it.dragboard.files.first().absolutePath
                }
            }
        }

    // https://www.bouncycastle.org/specifications.html
    val algs =
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
            //        "POLY1305" to listOf("AES", "CAMELLIA", "CAST6", "NOEKEON", "SEED", "SERPENT",
            // "Twofish"),
            "AESCMAC" to listOf("256"),
            "BLOWFISHCMAC" to listOf("256"),
            "DESCMAC" to listOf("256"),
            "DESEDECMAC" to listOf("256"),
            "SEED-CMAC" to listOf("256"),
            "Shacal-2CMAC" to listOf("256"),
            "SM4-CMAC" to listOf("256"),
            "Threefish" to listOf("256CMAC", "512CMAC", "1024CMAC"),
            //        "AESCCMMAC" to listOf("256"),
            //        "VMPCMAC" to listOf("256"),
            )
    private val selectedAlgItem = SimpleStringProperty(algs.keys.first())
    private val selectedBits = SimpleStringProperty(algs.values.first().first())
    lateinit var cbBits: ComboBox<String>
    private val info
        get() = "MAC: $method"

    override val root = vbox {
        paddingAll = 8
        label("待处理:") { paddingAll = 8 }
        input =
            textarea("HelloWorld") {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            paddingAll = 8
            alignment = Pos.CENTER_LEFT
            label("算法:  ")
            combobox(selectedAlgItem, algs.keys.toMutableList()) { cellFormat { text = it } }
            label("长度:  ") { paddingAll = 8 }
            cbBits =
                combobox(selectedBits, algs.values.first()) {
                    cellFormat { text = it }
                    isDisable = true
                }
        }
        hbox {
            paddingAll = 8
            alignment = Pos.CENTER_LEFT
            label("key: ") { paddingAll = 8 }
            key = textfield("hmac_key") { promptText = "请输入key" }
            label("输出编码:") { paddingAll = 8 }
            togglegroup {
                spacing = 8.0
                paddingAll = 8
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
                cbBits.isDisable = algs[newValue]!!.size == 1
            }
        }
        selectedBits.addListener { _, _, newValue ->
            println("selectedBits __ $newValue")
            newValue?.run {
                method =
                    "${selectedAlgItem.get()}${newValue.takeIf { algs[selectedAlgItem.get()]!!.size > 1 } ?: ""}"
                        .replace("SHA2(?!=\\d{3})".toRegex(), "SHA")
                        .replace(
                            "(GOST3411-2012|SIPHASH(?=\\d-)|SIPHASH128|SHA3(?=\\d{3})|DSTU7564|Skein|Threefish)".toRegex(),
                            "$1-"
                        )
                println("算法 $method")
                if (inputText.isNotEmpty()) {
                    doMac()
                }
            }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = 8.0
            button("运行") { action { doMac() } }
            button("复制结果") { action { outputText.copy() } }
        }
        label("输出内容:") { paddingBottom = 8 }
        output =
            textarea {
                promptText = "结果"
                isWrapText = true
            }
        infoLabel = label { paddingTop = 8 }
    }

    private fun doMac() =
        runAsync { controller.mac(inputText, keyText, method, outputEncode) } ui
            {
                output.text = it
                infoLabel.text = info
            }
}
