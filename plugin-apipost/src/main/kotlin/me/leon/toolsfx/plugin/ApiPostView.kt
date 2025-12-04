@file:Suppress("UNCHECKED_CAST")

package me.leon.toolsfx.plugin

import java.io.File
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.layout.Priority
import javafx.scene.text.Text
import javafx.scene.web.WebView
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.leon.*
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.toolsfx.plugin.ApiConfig.restoreFromConfig
import me.leon.toolsfx.plugin.net.*
import me.leon.toolsfx.plugin.net.HttpUrlUtil.POST_ACTION_DEFAULT
import me.leon.toolsfx.plugin.net.HttpUrlUtil.POST_ACTION_HEX
import me.leon.toolsfx.plugin.table.EditingCell
import me.leon.view.OnlineWebView
import tornadofx.*

private const val MAX_SHOW_LENGTH = 1_000_000

private const val TAB_STYLE = "-fx-base: lightblue;"

class ApiPostView : PluginFragment("ApiPost") {
    override val version = "v1.12.1"
    override val date: String = "2025-10-02"
    override val author = "Leon406"
    override val description = "ApiPost"

    init {
        println("Plugin Info:$description $version $date $author  ")
    }

    private val removedHeaderRegexp =
        "(?i)^(sec-|accept|dnt|connection|cache|host|pragma)".toRegex()

    private val controller: ApiPostController by inject()
    private var tfUrl: TextField by singleAssign()
    private var taReqHeaders: TextArea by singleAssign()
    private var taReqContent: TextArea by singleAssign()
    private var textRspStatus: Text by singleAssign()
    private var taRspHeaders: TextArea by singleAssign()
    private var taRspContent: TextArea by singleAssign()
    private var tfJsonPath: TextField by singleAssign()
    private var table: TableView<HttpParams> by singleAssign()
    private var tfRepeatNum: TextField by singleAssign()
    private var tfConcurrent: TextField by singleAssign()
    private var tfDelay: TextField by singleAssign()
    private var web: WebView by singleAssign()
    private var tgRsp: ToggleGroup by singleAssign()

    private val prettyProperty = SimpleBooleanProperty(true)
    private val hexProperty = SimpleBooleanProperty(false)
    private val showJsonPath = SimpleBooleanProperty(false)
    private val methods =
        mutableListOf(
            "POST",
            "GET",
            "PUT",
            "PATCH",
            "HEAD",
            "DELETE",
            "OPTIONS",
            "TRACE",
            "CONNECT",
        )
    private val bodyType = BodyType.entries.map { it.type }

    private val selectedMethod = SimpleStringProperty(methods.first())
    private val selectedBodyType = SimpleStringProperty(bodyType.first())
    private val showRspHeader = SimpleBooleanProperty(false)
    private val showRenderHtml = SimpleBooleanProperty(false)
    private val showReqHeader = SimpleBooleanProperty(false)
    private val showReqTable = SimpleBooleanProperty(false)
    private val running = SimpleBooleanProperty(false)
    private val requestParams = FXCollections.observableArrayList(HttpParams())
    private val showTableList = listOf("json", "form-data")
    private val fileKeys = arrayOf("file", "files", "image", "images")
    private val selectedUrl = SimpleStringProperty("plz set curl dir first!")
    private val reqHeaders
        get() = controller.parseHeaderString(taReqHeaders.text)

    private val reqTableParams
        get() =
            requestParams
                .filter { it.isEnable && it.key.isNotEmpty() && !it.isFile }
                .associate { it.key to it.value }
                .toMutableMap() as MutableMap<String, Any>

    private val uploadParams
        get() = requestParams.firstOrNull { it.isEnable && it.key.isNotEmpty() && it.isFile }

    private val eventHandler = fileDraggedHandler {
        table.selectionModel.selectedItem.valueProperty.value =
            it.joinToString(";") { it.absolutePath }
    }
    private val curlFileHandler = fileDraggedHandler {
        with(it.first()) {
            println(absolutePath)
            if (length() <= 128 * 1024) {
                if (realExtension() in unsupportedExts) {
                    println("unsupported file extension")
                } else {
                    resetUi(readText())
                }
            } else {
                println("not support file larger than 128KB")
            }
        }
    }
    private var response: Response? = null
    override val root = vbox {
        restoreFromConfig()
        prefWidth = 800.0
        spacing = 8.0
        paddingAll = 8
        hbox {
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            combobox(selectedMethod, methods)

            tfUrl =
                textfield("https://httpbin.org/anything") {
                    prefWidth = 400.0
                    promptText = "input your url"
                    onDragEntered = curlFileHandler
                }
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action {
                    selectedUrl.set("")
                    resetUi(clipboardText())
                }
            }
            button(graphic = imageview(IMG_RUN)) {
                enableWhen(!running)
                action { doRequest() }
            }

            button(graphic = imageview("/img/settings.png")) {
                action { openInternalWindow<SettingsView>() }
            }
            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { makeCurl().copy() }
            }
            button(graphic = imageview(IMG_SAVE)) {
                tooltip(messages["save"])
                action { saveRequestToFile() }
            }
            val configFile = ApiConfig.curlDir.toFile()
            val curlFiles =
                configFile
                    .walk()
                    .filter { it.isFile && it.extension == "curl" }
                    .map {
                        it.absolutePath
                            .replace(configFile.absolutePath, "")
                            .trimStart('/', '\\')
                            .replace(".curl", "")
                            .replace("\\", "/")
                    }
                    .toList()
            if (curlFiles.isNotEmpty()) {
                val newCurlFiles = curlFiles.toMutableList()
                newCurlFiles.add(0, "")
                selectedUrl.set("")
                combobox(selectedUrl, newCurlFiles) { cellFormat { text = it } }
                selectedUrl.addListener { _, _, newValue ->
                    if (newValue.isEmpty()) {
                        resetUi("https://httpbin.org/anything")
                        return@addListener
                    }
                    tfUrl.text = newValue as String
                    val curl = File(ApiConfig.curlDir.toFile(), "$newValue.curl")
                    resetUi(curl.readText())
                }
            }
            tfRepeatNum = textfield {
                promptText = "number"
                prefWidth = DEFAULT_SPACING_10X
                textFormatter = intTextFormatter
            }
            tfConcurrent = textfield {
                promptText = "concurrent"
                prefWidth = DEFAULT_SPACING_10X
                textFormatter = intTextFormatter
            }
            tfDelay = textfield {
                promptText = "delay"
                prefWidth = DEFAULT_SPACING_10X
                textFormatter = intTextFormatter
            }
        }

        hbox {
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            label("Request:")
            hbox {
                alignment = Pos.CENTER
                togglegroup {
                    togglebutton("Body") {
                        style = TAB_STYLE
                        action {
                            showReqHeader.value = false
                            showReqTable.value = selectedBodyType.get() in showTableList
                        }
                    }
                    togglebutton("Header") {
                        style = TAB_STYLE
                        action {
                            showReqHeader.value = true
                            showReqTable.value = false
                        }
                    }
                }
            }
            combobox(selectedBodyType, bodyType)
            selectedBodyType.addListener { _, _, newValue ->
                showReqTable.value = (newValue as String) in showTableList
            }

            button("Pretty") { action { taReqContent.text = taReqContent.text.prettyJson() } }
            button("Ugly") { action { taReqContent.text = taReqContent.text.uglyJson() } }
            button(graphic = imageview(IMG_ADD)) {
                visibleWhen(showReqTable)
                action { requestParams.add(HttpParams()) }
            }
            button(graphic = imageview(IMG_REMOVE)) {
                visibleWhen(showReqTable)
                action { requestParams.remove(table.selectionModel.selectedItem) }
            }
        }
        stackpane {
            spacing = 8.0
            prefHeight = 200.0
            taReqContent = textarea {
                promptText = "request body"
                isWrapText = true
                visibleWhen(!showReqHeader)
            }
            table =
                tableview(requestParams) {
                    visibleWhen(showReqTable)

                    column("isEnable", HttpParams::enableProperty).apply {
                        cellFactory = CheckBoxTableCell.forTableColumn(this)
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.1))
                    }

                    column("key", HttpParams::keyProperty).apply {
                        cellFactory = EditingCell.forTableColumn()
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.3))
                    }

                    column("value", HttpParams::valueProperty).apply {
                        cellFactory = EditingCell.forTableColumn()
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.5))
                        onDragEntered = eventHandler
                    }
                    column("isFile", HttpParams::fileProperty) {
                        cellFactory = CheckBoxTableCell.forTableColumn(this)
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.1))
                    }

                    isEditable = true
                }

            taReqHeaders = textarea {
                promptText = "request headers"
                isWrapText = true
                visibleWhen(showReqHeader)
                contextmenu {
                    item("Remove Headers") {
                        action {
                            taReqHeaders.text =
                                taReqHeaders.text
                                    .lines()
                                    .filter { it.isNotBlank() && !it.contains(removedHeaderRegexp) }
                                    .joinToString("\n")
                        }
                    }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_LEFT
            label("Response:")
            spacing = 8.0
            hbox {
                alignment = Pos.CENTER
                tgRsp = togglegroup {
                    togglebutton("Body") {
                        style = TAB_STYLE
                        action {
                            showRspHeader.value = false
                            showRenderHtml.value = false
                        }
                    }
                    togglebutton("Header") {
                        style = TAB_STYLE
                        action {
                            showRspHeader.value = true
                            showRenderHtml.value = false
                        }
                    }
                    togglebutton("Render") {
                        style = TAB_STYLE
                        action {
                            showRspHeader.value = false
                            showRenderHtml.value = true
                        }
                    }
                }
            }

            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { taRspContent.text.copy() }
            }

            button(graphic = imageview(IMG_SAVE)) {
                tooltip(messages["save"])
                action { saveResponseToFile() }
            }

            checkbox("pretty", prettyProperty)
            checkbox("hex", hexProperty)
            hexProperty.addListener { _, _, newValue ->
                HttpUrlUtil.addPostHandle(
                    if (newValue) {
                        POST_ACTION_HEX
                    } else {
                        POST_ACTION_DEFAULT
                    }
                )
            }
            checkbox("jsonpath", showJsonPath)
            tfJsonPath = textfield {
                visibleWhen(showJsonPath)
                promptText = "json path"
            }
        }
        stackpane {
            alignment = Pos.CENTER_RIGHT
            textRspStatus = text()
        }
        stackpane {
            prefHeight = 300.0
            vgrow = Priority.ALWAYS
            spacing = 8.0
            taRspHeaders = textarea {
                promptText = "response headers"
                isEditable = false
                isWrapText = true
                visibleWhen(showRspHeader)
            }
            taRspContent = textarea {
                promptText = "response body"
                isEditable = false
                isWrapText = true
                visibleWhen(!showRspHeader)
            }
            web = webview {
                engine.isJavaScriptEnabled = true
                engine.userStyleSheetLocation =
                    (OnlineWebView::class.java).getResource("/css/webview.css")?.toExternalForm()
                engine.onError = EventHandler { println("error ${it.message}") }
                engine.loadWorker.exceptionProperty().addListener { _, _, newValue ->
                    println("exception $newValue")
                }
                visibleWhen(showRenderHtml)
            }
        }
        title = "ApiPost"
    }

    private fun makeCurl(): String =
        Request(tfUrl.text, selectedMethod.get(), reqTableParams, reqHeaders, taReqContent.text)
            .apply {
                isJson = selectedBodyType.get() == BodyType.JSON.type
                requestParams
                    .firstOrNull { it.isEnable && it.key.isNotEmpty() && it.isFile }
                    ?.let { fileParamName = it.key }
            }
            .toCurl()

    private fun doRequest() {
        resetResponse()
        if (tfUrl.text.isEmpty() || !tfUrl.text.startsWith("http") && tfUrl.text.length < 11) {
            primaryStage.showToast("plz input legal url")
            return
        }
        running.value = true
        val count = runCatching { tfRepeatNum.text.toInt() }.getOrDefault(1)
        val concurrent = runCatching { tfConcurrent.text.toInt() }.getOrDefault(1)
        val delayMillis = runCatching { tfDelay.text.toLong() }.getOrDefault(0L)
        if (selectedBodyType.get() == BodyType.FORM_DATA.type) {
            reqHeaders["Content-Type"] = HttpUrlUtil.APPLICATION_URL_ENCODE
        }

        val dispatcher = Dispatchers.IO.limitedParallelism(concurrent)
        var lastResp: Response? = null
        runAsync {
            val start = System.currentTimeMillis()
            var success = 0
            val countMap: MutableMap<String, Int> = mutableMapOf()

            fun req() =
                runCatching {
                        if (selectedMethod.get() == "POST") {
                                val bodyType = bodyTypeMap[selectedBodyType.get()]
                                requireNotNull(bodyType)
                                when (bodyType) {
                                    BodyType.JSON,
                                    BodyType.FORM_DATA ->
                                        uploadParams?.run {
                                            controller.uploadFile(
                                                tfUrl.text,
                                                this.value.split(",", ";").map { it.toFile() },
                                                this.key,
                                                reqTableParams,
                                                reqHeaders,
                                            )
                                        }
                                            ?: controller.post(
                                                tfUrl.text,
                                                reqTableParams,
                                                reqHeaders,
                                                bodyType == BodyType.JSON,
                                            )

                                    BodyType.RAW ->
                                        controller.postRaw(
                                            tfUrl.text,
                                            taReqContent.text,
                                            reqHeaders,
                                        )
                                }
                            } else {
                                controller.request(
                                    tfUrl.text,
                                    selectedMethod.get(),
                                    reqTableParams,
                                    reqHeaders,
                                )
                            }
                            .also { lastResp = it }
                            .toLiteResponse()
                            .also {
                                if (it.code == 200) {
                                    success++
                                    countMap[it.hash] = countMap[it.hash]?.let { it + 1 } ?: 1
                                }
                            }
                    }
                    .onFailure {
                        Response(
                            -1,
                            (it.message ?: "").toByteArray(),
                            mutableMapOf(),
                            System.currentTimeMillis(),
                        )
                    }

            runCatching {
                    runBlocking {
                        (1..count)
                            .map {
                                async(dispatcher) {
                                    req().also {
                                        if (delayMillis > 0) {
                                            // delay 无法阻塞其他
                                            Thread.sleep(delayMillis)
                                        }
                                    }
                                }
                            }
                            .awaitAll()

                        lastResp!!
                    }
                }
                .onSuccess {
                    handleSuccess(it)
                    if (count > 1) {
                        ui {
                            val statisticInfo =
                                "  time  costs : ${System.currentTimeMillis() - start} ms" +
                                    "\nsuccess/total: $success/$count" +
                                    "\n    detail   :\n${
                                            countMap.map { "\t\tresp hash: ${it.key}  num: ${it.value}" }
                                                .joinToString(System.lineSeparator())
                                        }"
                            primaryStage.showToast(statisticInfo, 3000)
                            taRspContent.text = statisticInfo + "\n" + taRspContent.text
                        }
                    }
                }
                .onFailure {
                    textRspStatus.text = it.message
                    taRspHeaders.text = ""
                    taRspContent.text = it.stacktrace()
                    this@ApiPostView.running.value = false
                }
        }
    }

    private fun handleSuccess(resp: Response) {
        response = resp
        textRspStatus.text = resp.statusInfo
        taRspHeaders.text = resp.headerInfo
        val contentType = resp.headers["Content-Type"]?.toString().orEmpty()
        val rspString = resp.data.decodeToString()

        val properData =
            when {
                contentType.startsWith("audio") || contentType.startsWith("video") -> {
                    // fixme 不支持data协议, 只能播放一次
                    val tmpFile = File.createTempFile("apipost", "")
                    val tag = contentType.substringBefore("/")
                    tmpFile.deleteOnExit()
                    tmpFile.writeBytes(resp.data)
                    "<$tag controls src=\"file://${tmpFile.absolutePath}\"></$tag>"
                }

                contentType.startsWith("image/svg+xml") -> rspString
                contentType.startsWith("image") ->
                    "<img src=\"data:$contentType;base64,${resp.data.base64()}\">"

                else -> rspString
            }
        val binaryPreivew =
            contentType.startsWith("image") ||
                contentType.startsWith("audio") ||
                contentType.startsWith("video")
        if (binaryPreivew) {
            tgRsp.toggles.last().isSelected = true
            showRenderHtml.value = true
            showRspHeader.value = false
        } else {
            showRenderHtml.value = false
        }

        runOnUi {
            println("data $contentType: $properData")
            web.engine.loadContent(properData)
        }

        if (resp.length > MAX_SHOW_LENGTH || resp.data.size > MAX_SHOW_LENGTH) {
            taRspContent.text = "Data is Too Large! ${resp.length} "
            running.value = false
            return
        }
        val showData =
            when {
                binaryPreivew -> "See Render Tab"
                showJsonPath.get() && tfJsonPath.text.trim().isNotEmpty() ->
                    runCatching { rspString.simpleJsonPath(tfJsonPath.text.trim()) }
                        .getOrElse { rspString }
                else -> rspString
            }

        taRspContent.text =
            if (prettyProperty.get()) {
                showData.unicodeMix2String().prettyJson()
            } else {
                showData
            }

        running.value = false
    }

    private fun resetUi(clipboardText: String) {
        clipboardText.parseCurl().run {
            selectedMethod.value = method
            tfUrl.text = url
            taReqHeaders.text = headers.entries.joinToString("\n") { "${it.key}: ${it.value}" }
            if (params.isNotEmpty()) {
                showReqTable.value = true
                showReqHeader.value = false
                showRenderHtml.value = false
                selectedBodyType.value = bodyType[1]

                val tmpParam =
                    params.entries
                        .fold(mutableListOf<HttpParams>()) { acc, mutableEntry ->
                            acc.apply {
                                add(
                                    HttpParams().apply {
                                        keyProperty.value = mutableEntry.key
                                        valueProperty.value = mutableEntry.value.toString()
                                        fileProperty.value =
                                            mutableEntry.key in fileKeys ||
                                                mutableEntry.value.toString() == "@file"
                                    }
                                )
                            }
                        }
                        .distinct()

                requestParams.clear()
                requestParams.addAll(tmpParam)
            } else {
                taReqContent.text = rawBody
                selectedBodyType.value = BodyType.RAW.type
                showReqTable.value = false
            }
        }
    }

    private fun saveResponseToFile() {
        response?.let { resp ->
            chooseFile(
                    "Save to File",
                    arrayOf(FileChooser.ExtensionFilter("All", "*.*")),
                    File(System.getProperty("user.home")),
                    FileChooserMode.Save,
                )
                .firstOrNull()
                ?.let { file ->
                    runAsync {
                        runCatching {
                                file.writeBytes(resp.data)
                                runOnUi { primaryStage.showToast("Success: ${file.absolutePath}") }
                            }
                            .onFailure {
                                runOnUi { primaryStage.showToast("Failed: ${it.message}") }
                            }
                    }
                }
        } ?: run { primaryStage.showToast("没有可保存的响应数据") }
    }

    private fun saveRequestToFile() {
        chooseFile(
                "Save to File",
                arrayOf(FileChooser.ExtensionFilter("All", "*.curl")),
                ApiConfig.curlDir.toFile(),
                FileChooserMode.Save,
            )
            .firstOrNull()
            ?.let { file ->
                runAsync {
                    runCatching {
                            file.writeText(makeCurl())
                            runOnUi { primaryStage.showToast("Success: ${file.absolutePath}") }
                        }
                        .onFailure { runOnUi { primaryStage.showToast("Failed: ${it.message}") } }
                }
            }
    }

    private fun resetResponse() {
        response = null
        taRspContent.text = ""
        taRspHeaders.text = ""
        textRspStatus.text = ""

        showRspHeader.value = false
        showRenderHtml.value = false
        tgRsp.toggles.first().isSelected = true
    }
}
