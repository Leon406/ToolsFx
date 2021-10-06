package me.leon.toolsfx.plugin

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.text.Text
import me.leon.ext.*
import me.leon.toolsfx.plugin.net.*
import tornadofx.*

class ApiPostView : PluginView("ApiPost") {
    override val version = "v1.0.0.beta"
    override val date: String = "2021-10-06"
    override val author = "Leon406"
    override val description = "ApiPost"
    private val controller: ApiPostController by inject()
    private lateinit var tfUrl: TextField
    private lateinit var taReqHeaders: TextArea
    private lateinit var taReqContent: TextArea
    private lateinit var textRspStatus: Text
    private lateinit var taRspHeaders: TextArea
    private lateinit var taRspContent: TextArea
    private lateinit var table: TableView<HttpParams>
    private val methods =
        mutableListOf(
            "POST",
            "GET",
            "PUT",
            "PATCH",
            "HEAD",
            "DELETE",
            "OPTIONS",
        )
    private val bodyType = BodyType.values().map { it.type }

    private val selectedMethod = SimpleStringProperty(methods.first())
    private val selectedBodyType = SimpleStringProperty(bodyType.first())
    private val showRspHeader = SimpleBooleanProperty(false)
    private val showReqHeader = SimpleBooleanProperty(false)
    private val showReqTable = SimpleBooleanProperty(false)
    private var requestParams = FXCollections.observableArrayList<HttpParams>()
    private val showTableList = listOf("json", "form-data")

    private val reqHeaders
        get() = controller.parseHeaderString(taReqHeaders.text)
    private val reqTableParams
        get() =
            requestParams
                .filter { it.isEnable == "true" && it.key.isNotEmpty() }
                .associate { it.key to it.value }
                .toMutableMap()
    private val uploadParams
        get() =
            requestParams.firstOrNull {
                it.isEnable == "true" && it.key.isNotEmpty() && it.isFile == "true"
            }

    private val eventHandler = fileDraggedHandler {
        with(it.first()) {
            println(absolutePath)

            table.selectionModel.selectedItem.also { println(it) }.value = absolutePath
        }
    }
    override val root = vbox {
        prefWidth = 800.0
        spacing = 8.0
        paddingAll = 8
        hbox {
            spacing = 8.0
            combobox(selectedMethod, methods)

            tfUrl =
                textfield {
                    prefWidth = 400.0
                    promptText = "input your url"
                }

            button("request") {
                action {
                    val req =
                        if (selectedMethod.get() == "POST")
                            when (bodyTypeMap[selectedBodyType.get()]) {
                                BodyType.JSON, BodyType.FORM_DATA ->
                                    uploadParams?.run {
                                        controller.uploadFile(
                                            tfUrl.text,
                                            listOf(this.value.toFile()),
                                            this.key,
                                            reqTableParams as MutableMap<String, Any>,
                                            reqHeaders,
                                        )
                                    }
                                        ?: controller.post(
                                            tfUrl.text,
                                            reqTableParams as MutableMap<String, Any>,
                                            reqHeaders,
                                            bodyTypeMap[selectedBodyType.get()] == BodyType.JSON
                                        )
                                else ->
                                    controller.postRaw(tfUrl.text, taReqContent.text, reqHeaders)
                            }
                        else
                            controller.request(
                                tfUrl.text,
                                selectedMethod.get(),
                                reqTableParams as MutableMap<String, Any>,
                                reqHeaders,
                            )
                    req.run {
                        textRspStatus.text = statusInfo
                        taRspHeaders.text = headerInfo
                        taRspContent.text = data
                    }
                }
            }
        }

        hbox {
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            label("Request:")
            button("Body") {
                action {
                    showReqHeader.value = false
                    showReqTable.value = selectedBodyType.get() in showTableList
                }
            }
            button("Header") {
                action {
                    showReqHeader.value = true
                    showReqTable.value = false
                }
            }
            combobox(selectedBodyType, bodyType)
            selectedBodyType.addListener { _, _, newValue ->
                println(newValue)
                showReqTable.value = (newValue as String) in showTableList
            }
            button("add") {
                visibleWhen(showReqTable)
                action { requestParams.add(HttpParams()) }
            }
            button("remove") {
                visibleWhen(showReqTable)
                action { requestParams.remove(table.selectionModel.selectedItem) }
            }
        }
        stackpane {
            spacing = 8.0

            taReqContent =
                textarea() {
                    promptText = "request params"
                    visibleWhen(!showReqHeader)
                }
            table =
                tableview(requestParams) {
                    prefHeight = 200.0
                    visibleWhen(showReqTable)
                    column("isEnable", HttpParams::isEnable).apply {
                        cellFactory = TextFieldTableCell.forTableColumn<HttpParams>()
                    }
                    column("key", HttpParams::key).apply {
                        cellFactory = TextFieldTableCell.forTableColumn<HttpParams>()
                        prefWidth = 200.0
                    }
                    column("value", HttpParams::value).apply {
                        cellFactory = TextFieldTableCell.forTableColumn<HttpParams>()
                        prefWidth = 200.0
                        onDragEntered = eventHandler
                    }
                    column("isFile", HttpParams::isFile).apply {
                        cellFactory = TextFieldTableCell.forTableColumn()
                        prefWidth = 100.0
                    }
                    isEditable = true
                }
            taReqHeaders =
                textarea {
                    promptText = "request headers"
                    visibleWhen(showReqHeader)
                }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            label("Response:")
            spacing = 8.0
            button("Body") { action { showRspHeader.value = false } }
            button("Header") { action { showRspHeader.value = true } }
        }
        stackpane {
            alignment = Pos.CENTER_RIGHT
            textRspStatus = text()
        }
        stackpane {
            prefHeight = 300.0
            spacing = 8.0
            taRspHeaders =
                textarea {
                    promptText = "response headers"
                    isEditable = false
                    visibleWhen(showRspHeader)
                }
            taRspContent =
                textarea {
                    promptText = "response content"
                    isEditable = false
                    isWrapText = true
                    visibleWhen(!showRspHeader)
                }
        }
        title = "ApiPost"
    }
}
