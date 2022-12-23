package me.leon.toolsfx.plugin

import java.awt.image.BufferedImage
import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import me.leon.*
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.toolsfx.plugin.ext.*
import tornadofx.*

class ImageProcessView : PluginFragment("ImageProcessView") {
    override val version = "v1.2.0"
    override val date: String = "2022-12-23"
    override val author = "Leon406"
    override val description = "图片模块"
    private var taInput: TextArea by singleAssign()
    private var ivInput: ImageView by singleAssign()
    private var taOutput: TextArea by singleAssign()
    private var ivOutput: ImageView by singleAssign()
    private var param1: TextField by singleAssign()
    private var cbParam: ComboBox<String> by singleAssign()

    private val fileMode = SimpleBooleanProperty(true)
    private val showInputImage = SimpleBooleanProperty(false)
    private val showImage = SimpleBooleanProperty(false)
    private val showParams = SimpleBooleanProperty(false)
    private val showComboParam = SimpleBooleanProperty(false)
    private val selectedParam = SimpleStringProperty("")

    private val paramsMap: Map<String, String>
        get() = mutableMapOf(P1 to param1.text, C1 to selectedParam.get())

    private val eventHandler = fileDraggedHandler {
        taInput.text =
            if (fileMode.get()) {
                it.first().absolutePath
            } else {
                it.first().properText()
            }
    }
    private var imageServiceType: ImageServiceType = ImageServiceType.FIX_PNG
    private val controller: ImageController by inject()

    private val inputText: String
        get() = taInput.text.trim()
    private val outputText: String
        get() = taOutput.text

    override val root = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        title = "ImageProcessView"
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            label(messages["input"])
            checkbox("文件模式", fileMode)
            checkbox("显示图片", showInputImage) {
                selectedProperty().addListener { _, _, newValue ->
                    if (newValue) {
                        val file = taInput.text.toFile()
                        if (file.exists() && EXTENSION_IMAGE.contains(file.realExtension())) {
                            ivInput.image = file.toImage()
                        } else {
                            ivInput.image = null
                        }
                    } else {
                        ivInput.image = null
                    }
                }
            }
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taInput.text = clipboardText() }
            }
        }

        stackpane {
            taInput = textarea {
                isWrapText = true
                onDragEntered = eventHandler
                visibleWhen(!showInputImage)
            }
            ivInput = imageview()
            scrollpane(true) {
                visibleWhen(showInputImage)
                content = ivInput
            }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = DEFAULT_SPACING
            paddingBottom = DEFAULT_SPACING
            spacing = DEFAULT_SPACING
            label("function:")
            tilepane {
                hgap = 8.0
                vgap = 8.0
                alignment = Pos.TOP_LEFT
                prefColumns = 5
                togglegroup {
                    serviceTypeMap.forEach {
                        radiobutton(it.key) {
                            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
                            if (it.value == ImageServiceType.FIX_PNG) isSelected = true
                        }
                    }
                    selectedToggleProperty().addListener { _, _, new ->
                        imageServiceType = new.cast<RadioButton>().text.locationServiceType()
                        val paramHints = imageServiceType.paramsHints()
                        showParams.value = paramHints.isNotEmpty()
                        param1.promptText = "".takeIf { paramHints.isEmpty() } ?: paramHints.first()

                        val options = imageServiceType.options()
                        if (options.isNotEmpty()) {
                            showComboParam.value = options.isNotEmpty()
                            cbParam.items = options.toMutableList().asObservable()
                            selectedParam.set(if (options.isEmpty()) "" else options.first())
                            cbParam.bind(selectedParam)
                        }

                        println(imageServiceType)
                        println(
                            "params ${paramHints.contentToString()} options ${options.contentToString()}"
                        )
                    }
                }
            }
        }

        hbox {
            addClass(Styles.group, Styles.left)
            cbParam = combobox(selectedParam) { visibleWhen(showComboParam) }
            param1 = textfield { visibleWhen(showParams) }
        }
        hbox {
            alignment = Pos.CENTER_LEFT
            spacing = DEFAULT_SPACING
            paddingLeft = DEFAULT_SPACING
            button(messages["run"], imageview(IMG_RUN)) { action { doProcess() } }
        }
        hbox {
            spacing = DEFAULT_SPACING
            alignment = Pos.CENTER_LEFT
            label(messages["output"])
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action {
                    if (showImage.get()) {
                        ivOutput.image.copy()
                    } else {
                        outputText.copy()
                    }
                }
            }
            button(graphic = imageview(IMG_UP)) {
                tooltip(messages["up"])
                action {
                    taInput.text = outputText
                    taOutput.text = ""
                }
            }
            button("stegSolve") { action { startStegSolve() } }
        }

        stackpane {
            taOutput = textarea {
                promptText = messages["outputHint"]
                isWrapText = true
                visibleWhen(!showImage)
            }
            ivOutput = imageview()
            scrollpane(true) {
                visibleWhen(showImage)
                content = ivOutput
            }
        }
    }

    private fun doProcess() {
        if (inputText.isEmpty()) return
        runAsync { controller.process(imageServiceType, inputText, fileMode.get(), paramsMap) } ui
            {
                showImage.value = it !is String
                when (it) {
                    is String -> taOutput.text = it
                    is ByteArray -> ivOutput.image = Image(it.inputStream())
                    is BufferedImage -> ivOutput.image = it.toFxImg()
                    is Image -> ivOutput.image = it
                }
            }
    }

    companion object {
        private val javaHome = System.getProperty("java.home")
        private val userDir = System.getProperty("user.dir")
        private var command: Array<String>? = null

        init {
            userDir
                .toFile()
                .walk()
                .find { it.extension == "jar" && it.name.startsWith("StegSolve") }
                ?.run {
                    command =
                        arrayOf(
                            "$javaHome${File.separator}bin${File.separator}java",
                            "-jar",
                            "-Dsun.java2d.uiScale=${ToolsApp.scale}",
                            absolutePath
                        )
                }
        }

        private fun startStegSolve() = Runtime.getRuntime().exec(command)
    }
}
