package me.leon.toolsfx.plugin

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import me.leon.ext.fx.fileDraggedHandler
import me.leon.toolsfx.plugin.ApiConfig.saveConfig
import me.leon.toolsfx.plugin.net.TrustManager
import tornadofx.*

class SettingsView : View("Setting") {

    private val proxies = listOf("HTTP", "SOCKS4", "SOCKS5")
    private val selectedProxy = SimpleStringProperty(ApiConfig.proxyType)
    private val isEnableProxy = SimpleBooleanProperty(ApiConfig.isEnableProxy)
    private val isP12 = SimpleBooleanProperty(false)
    private lateinit var taHeaders: TextArea
    private lateinit var tfIp: TextField
    private lateinit var tfPort: TextField
    private lateinit var tfTime: TextField
    private lateinit var tfCerPath: TextField
    private lateinit var tfCerPass: TextField
    private val eventHandler = fileDraggedHandler {
        with(it.first()) {
            println(absolutePath)
            tfCerPath.text = absolutePath
        }
    }
    override val root = vbox {
        paddingAll = 8
        spacing = 8.0
        alignment = Pos.CENTER
        label("TimeOut")
        tfTime = textfield(ApiConfig.timeOut.toString()) { promptText = "global header" }
        label("Certification")
        hbox {
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            checkbox("p12", isP12)
            tfCerPath =
                textfield {
                    promptText = "file path(drag file here)"
                    onDragEntered = eventHandler
                }

            tfCerPass =
                textfield {
                    enableWhen(isP12)
                    promptText = "pkcs12 password"
                }
        }
        label("Proxy")
        hbox {
            spacing = 8.0
            alignment = Pos.CENTER
            combobox(selectedProxy, proxies)
            tfIp =
                textfield(ApiConfig.proxyHost) {
                    enableWhen(isEnableProxy)
                    promptText = "ip"
                }
            tfPort =
                textfield(ApiConfig.proxyPort) {
                    enableWhen(isEnableProxy)
                    promptText = "port"
                }
            checkbox("enable", isEnableProxy)
        }
        label("Global Headers")
        taHeaders = textarea(ApiConfig.globalHeaders) { promptText = "global header" }

        button("apply") {
            action {
                if (tfCerPath.text.isNotEmpty()) {
                    if (isP12.get()) TrustManager.parseFromPkcs12(tfCerPath.text, tfCerPass.text)
                    else TrustManager.parseFromCertification(tfCerPath.text)
                }
                saveConfig(
                    isEnableProxy.get(),
                    taHeaders.text,
                    selectedProxy.get(),
                    tfIp.text,
                    tfPort.text,
                    tfTime.text.toInt()
                )
                close()
            }
        }
    }
}
