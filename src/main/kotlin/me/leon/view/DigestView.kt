package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import me.leon.ext.copy
import tornadofx.*

class DigestView : View("哈希(摘要)") {
    private val controller: ToolController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val fileHash = SimpleBooleanProperty(false)
    private lateinit var input: TextArea
    lateinit var output: TextArea
    private val inputText: String
        get() = input.text
    private val outputText: String
        get() = output.text
    var method = "MD5"


    private val eventHandler = EventHandler<DragEvent> {
        println("${it.dragboard.hasFiles()}______" + it.eventType)
        if (it.eventType.name == "DRAG_ENTERED") {
            if (it.dragboard.hasFiles()) {
                println(it.dragboard.files)
                input.text = it.dragboard.files.first().absolutePath
            }
        }
    }
    val algs = linkedMapOf(
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
    )
    private val selectedAlgItem = SimpleStringProperty(algs.keys.first())
    private val selectedBits = SimpleStringProperty(algs.values.first().first())

    lateinit var cbBits: ComboBox<String>
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
            alignment = Pos.CENTER_LEFT
            label("算法:  ")
            combobox(selectedAlgItem, algs.keys.toMutableList()) {
                cellFormat {
                    text = it
                }
            }

            label("长度:  ") {
                paddingAll = 8
            }
            cbBits = combobox(selectedBits, algs.values.first()) {
                cellFormat {
                    text = it
                }
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

        selectedBits.addListener { _, oldValue, newValue ->
            println("selectedBits __ $newValue")
            newValue?.run {
                method = "${selectedAlgItem.get()}${newValue.takeIf { algs[selectedAlgItem.get()]!!.size > 1 } ?: ""}"
                    .replace("SHA2", "SHA-").replace("SHA3", "SHA3-")
                println("算法 $method")
                if (inputText.isNotEmpty() && !fileHash.get()) {
                    runAsync {
                        controller.digest(method, inputText)
                    } ui {
                        output.text = it
                    }
                }
            }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            button("运行") {
                action {
                    runAsync {
                        if (fileHash.get())
                            controller.digestFile(method, inputText)
                        else controller.digest(method, inputText)
                    } ui {
                        output.text = it
                    }
                }
            }

            checkbox("文件", fileHash) {
                paddingAll = 8
            }

            fileHash.addListener { _, _, newValue ->
                println("fileHash__ $newValue")
                if (newValue) {
                    println("____dddd")
                    controller.digestFile(method, inputText)
                } else {
                    controller.digest(method, inputText)
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
}