package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.controller.MacController
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import tornadofx.*

class MacView : View("MAC") {
    private val controller: MacController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val enableIv = SimpleBooleanProperty(false)
    private val enableBits = SimpleBooleanProperty(false)
    private lateinit var taInput: TextArea
    private lateinit var tfKey: TextField
    private lateinit var tfIv: TextField
    private lateinit var labelInfo: Label
    private lateinit var taOutput: TextArea
    private val inputText: String
        get() = taInput.text
    private val outputText: String
        get() = taOutput.text
    private var method = "HmacMD5"
    private var outputEncode = "hex"
    private val regAlgReplace =
        "(POLY1305|GOST3411-2012|SIPHASH(?=\\d-)|SIPHASH128|SHA3(?=\\d{3})|DSTU7564|Skein|Threefish)".toRegex()
    private val eventHandler = fileDraggedHandler { taInput.text = it.first().absolutePath }

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
    private var keyEncode = "raw"
    private var ivEncode = "raw"
    private val keyByteArray
        get() =
            when (keyEncode) {
                "raw" -> tfKey.text.toByteArray()
                "hex" -> tfKey.text.hex2ByteArray()
                "base64" -> tfKey.text.base64Decode()
                else -> byteArrayOf()
            }

    private val ivByteArray
        get() =
            when (ivEncode) {
                "raw" -> tfIv.text.toByteArray()
                "hex" -> tfIv.text.hex2ByteArray()
                "base64" -> tfIv.text.base64Decode()
                else -> byteArrayOf()
            }
    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["input"])
            button(graphic = imageview("/img/import.png")) {
                action { taInput.text = clipboardText() }
            }
        }
        taInput =
            textarea() {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            label(messages["alg"])
            combobox(selectedAlgItem, algs.keys.toMutableList()) { cellFormat { text = it } }
            label(messages["bits"]) { paddingAll = DEFAULT_SPACING }
            cbBits =
                combobox(selectedBits, algs.values.first()) {
                    cellFormat { text = it }
                    enableWhen(enableBits)
                }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            label("key:")
            tfKey = textfield { promptText = messages["keyHint"] }
            vbox {
                togglegroup {
                    spacing = DEFAULT_SPACING
                    paddingAll = DEFAULT_SPACING
                    radiobutton("raw") { isSelected = true }
                    radiobutton("hex")
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        keyEncode = new.cast<RadioButton>().text
                    }
                }
            }
            label("iv:")
            tfIv =
                textfield {
                    promptText = messages["ivHint"]
                    enableWhen(enableIv)
                }
            vbox {
                enableWhen(enableIv)
                togglegroup {
                    spacing = DEFAULT_SPACING
                    paddingAll = DEFAULT_SPACING
                    radiobutton("raw") { isSelected = true }
                    radiobutton("hex")
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        ivEncode = new.cast<RadioButton>().text
                    }
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
            alignment = Pos.TOP_LEFT
            hgap = DEFAULT_SPACING
            label(messages["outputEncoding"])
            togglegroup {
                radiobutton("hex") { isSelected = true }
                radiobutton("base64")
                selectedToggleProperty().addListener { _, _, new ->
                    outputEncode = new.cast<RadioButton>().text
                }
            }
            button(messages["run"], imageview("/img/run.png")) {
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                action { doMac() }
            }
        }
        hbox {
            label(messages["output"])
            button(graphic = imageview("/img/copy.png")) { action { outputText.copy() } }
        }
        taOutput =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
            }
    }

    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun doMac() =
        runAsync {
            if (method.contains("POLY1305|-GMAC".toRegex()))
                controller.macWithIv(inputText, keyByteArray, ivByteArray, method, outputEncode)
            else controller.mac(inputText, keyByteArray, method, outputEncode)
        } ui
            {
                taOutput.text = it
                labelInfo.text = info
                if (Prefs.autoCopy)
                    outputText.copy().also { primaryStage.showToast(messages["copied"]) }
            }
}
