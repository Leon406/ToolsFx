package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.DragEvent
import me.leon.base.base64Decode
import me.leon.ext.copy
import me.leon.ext.hex2ByteArray
import tornadofx.*

class SymmetricCryptoView : View("对称加密") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isFile = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var key: TextField
    private lateinit var iv: TextField
    var isEncrypt = true
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text
    var method = "MD5"

    private val keyByteArray
        get() = when (keyEncode) {
            "raw" -> key.text.toByteArray()
            "hex" -> key.text.hex2ByteArray()
            "base64" -> key.text.base64Decode()
            else-> byteArrayOf()
        }

    var keyEncode = "raw"
    var ivEncode = "raw"

    private val ivByteArray
        get() = when (ivEncode) {
            "raw" -> iv.text.toByteArray()
            "hex" -> iv.text.hex2ByteArray()
            "base64" -> iv.text.base64Decode()
            else->byteArrayOf()
        }

    private val eventHandler = EventHandler<DragEvent> {
        println("${it.dragboard.hasFiles()}______" + it.eventType)
        if (it.eventType.name == "DRAG_ENTERED") {
            if (it.dragboard.hasFiles()) {
                println(it.dragboard.files)
                input.text = it.dragboard.files.first().absolutePath
            }
        }
    }
    private val algs = mutableListOf(
        "DES",
        "DESEDE",
        "AES",
        "SM4",
        "Blowfish",
        "Twofish",
        "RC2"
    )
    private val paddingsAlg = mutableListOf(
        "PKCS5Padding", "PKCS7Padding", "ISO10126Padding", "ZeroBytePadding",
        "NoPadding", "TBCPadding",
        "X923Padding", "ISO7816d4Padding",
        "ISO10126d2Padding"
    )
    private val modes = mutableListOf("CBC", "ECB", "CFB", "OFB", "CTR", "GCM")
    private val selectedAlg = SimpleStringProperty(algs[2])
    private val selectedPadding = SimpleStringProperty(paddingsAlg.first())
    private val selectedMod = SimpleStringProperty(modes.first())

    private val cipher
        get() = "${selectedAlg.get()}/${selectedMod.get()}/${selectedPadding.get()}"

    override val root = vbox {
        paddingAll = 8


        label("待处理:") {
            paddingAll = 8
        }

        input = textarea {
            promptText = "请输入内容或者拖动文件到此区域"
            isWrapText = true
            onDragEntered = eventHandler
        }
        hbox {
            paddingAll = 8
            alignment = Pos.BASELINE_CENTER
            label("算法:") {
                paddingAll = 8
            }
            combobox(selectedAlg, algs) {
                cellFormat {
                    text = it
                }
            }

            label("mode:") {
                paddingAll = 8
            }
            combobox(selectedMod, modes) {
                cellFormat {
                    text = it
                }
            }

            label("padding:") {
                paddingAll = 8
            }
            combobox(selectedPadding, paddingsAlg) {
                cellFormat {
                    text = it
                }
            }
        }

        hbox {

            alignment = Pos.CENTER_LEFT
            paddingAll = 8
            label("key:") {
                paddingAll = 8
            }
            key = textfield {
                promptText = "请输入key"
            }
            vbox {
                togglegroup {
                    spacing = 8.0
                    paddingAll = 8
                    radiobutton("raw") {
                        isSelected = true
                    }
                    radiobutton("hex")
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        keyEncode = (new as RadioButton).text
                    }
                }
            }


            label("iv:") {
                paddingAll = 8
            }
            iv = textfield {
                promptText = "请输入iv"
            }
            vbox {
                togglegroup {
                    spacing = 8.0
                    paddingAll = 8
                    radiobutton("raw") {
                        isSelected = true
                    }
                    radiobutton("hex")
                    radiobutton("base64")
                    selectedToggleProperty().addListener { _, _, new ->
                        ivEncode = (new as RadioButton).text
                    }
                }
            }
        }
        selectedAlg.addListener { _, _, newValue ->
            newValue?.run {
                println("alg $newValue")
            }
        }

        selectedMod.addListener { _, _, newValue ->
            newValue?.run {
                println("cipher $newValue")
            }
        }

        selectedPadding.addListener { _, _, newValue ->
            newValue?.run {
                println("padding $newValue")
            }
        }

        hbox {
            alignment = Pos.CENTER_LEFT

            togglegroup {
                spacing = 8.0
                alignment = Pos.BASELINE_CENTER
                radiobutton("加密") {
                    isSelected = true
                }
                radiobutton("解密")
                selectedToggleProperty().addListener { _, _, new ->
                    isEncrypt = (new as RadioButton).text == "加密"
                    doCrypto()
                }
            }
            button("运行") {
                action {
                    doCrypto()
                }
            }
            // TODO: 2021/7/6 0006 文件支持
            checkbox("文件", isFile)

            isFile.addListener { _, _, newValue ->
                println("fileHash__ $newValue")
                if (newValue) {
                    println("____dddd")
                    controller.digestFile(method, inputText)
                } else {
                    controller.digest(method, inputText)
                }
            }

            button("上移") {
                action {
                    input.text = outputText
                    output.text = ""
                }
            }

            button("复制结果") {
                action {
                    outputText.copy()
                }
            }
        }
        label("输出内容:") {
            paddingBottom = 8
        }
        output = textarea {
            promptText = "结果"
            isWrapText = true
        }
    }

    private fun doCrypto() {
        if (isEncrypt) {
            runAsync {
                if (isFile.get())
                    controller.encryptByFile(keyByteArray, inputText, ivByteArray, cipher)
                else controller.encrypt(keyByteArray, inputText, ivByteArray, cipher)
            } ui {
                output.text = it
            }
        } else {
            runAsync {
                if (isFile.get())
                    controller.decryptByFile(keyByteArray, inputText, ivByteArray, cipher)
                else controller.decrypt(keyByteArray, inputText, ivByteArray, cipher)
            } ui {
                output.text = it
            }
        }
    }
}