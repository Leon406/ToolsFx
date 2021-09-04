package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.input.DragEvent
import me.leon.controller.DigestController
import me.leon.ext.DEFAULT_SPACING
import me.leon.ext.clipboardText
import me.leon.ext.copy
import tornadofx.*

class DigestView : View("哈希") {
    private val controller: DigestController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val fileHash = SimpleBooleanProperty(false)
    private val isProcessing = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    private lateinit var infoLabel: Label
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text
    var method = "MD5"

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
    private val algs =
        linkedMapOf(
            "MD5" to listOf("128"),
            "MD4" to listOf("128"),
            "MD2" to listOf("128"),
            "SM3" to listOf("256"),
            "Tiger" to listOf("192"),
            "Whirlpool" to listOf("512"),
            "SHA1" to listOf("160"),
            "SHA2" to listOf("224", "256", "384", "512", "512/224", "512/256"),
            "SHA3" to listOf("224", "256", "384", "512"),
            "RIPEMD" to listOf("128", "160", "256", "320"),
            "Keccak" to listOf("224", "256", "288", "384", "512"),
            "Blake2b" to listOf("160", "256", "384", "512"),
            "Blake2s" to listOf("160", "224", "256"),
            "DSTU7564" to listOf("256", "384", "512"),
            "Skein" to
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
            "GOST3411" to listOf("256"),
            "GOST3411-2012" to listOf("256", "512"),
            "Haraka" to listOf("256", "512"),
        )
    private val selectedAlgItem = SimpleStringProperty(algs.keys.first())
    private val selectedBits = SimpleStringProperty(algs.values.first().first())
    lateinit var cbBits: ComboBox<String>
    private val info
        get() = "Hash: $method bits: ${selectedBits.get()}  file mode: ${fileHash.get()}"

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label("待处理:")
            button("剪贴板导入") { action { input.text = clipboardText() } }
        }
        input =
            textarea {
                promptText = "请输入内容或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            label("算法:  ")
            combobox(selectedAlgItem, algs.keys.toMutableList()) { cellFormat { text = it } }
            label("长度:  ")
            cbBits =
                combobox(selectedBits, algs.values.first()) {
                    cellFormat { text = it }
                    isDisable = true
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
                        .replace("SHA2", "SHA-")
                        .replace(
                            "(Haraka|GOST3411-2012|Keccak|SHA3|Blake2b|Blake2s|DSTU7564|Skein)".toRegex(),
                            "$1-"
                        )
                println("算法 $method")
                if (inputText.isNotEmpty() && !fileHash.get()) {
                    doHash()
                }
            }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            paddingLeft = DEFAULT_SPACING
            checkbox("文件模式", fileHash)
            button("运行") {
                enableWhen(!isProcessing)
                action { doHash() }
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
        bottom = hbox { infoLabel = label() }
    }

    private fun doHash() =
        runAsync {
            isProcessing.value = true
            if (fileHash.get()) controller.digestFile(method, inputText)
            else controller.digest(method, inputText)
        } ui
            {
                isProcessing.value = false
                output.text = it
                infoLabel.text = info
            }
}
