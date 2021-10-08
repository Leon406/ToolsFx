package me.leon.toolsfx.plugin

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import me.leon.toolsfx.plugin.ApiConfig.saveConfig
import tornadofx.*

class SettingsView : View("Setting") {

    private val proxies = listOf("DIRECT", "SOCKET4", "SOCKET5", "HTTP")
    private val selectedProxy = SimpleStringProperty(ApiConfig.proxyType)
    private val isEnableProxy = SimpleBooleanProperty(ApiConfig.isEnableProxy)
    private lateinit var taHeaders: TextArea
    private lateinit var tfIp: TextField
    private lateinit var tfPort: TextField
    override val root = vbox {
        paddingAll = 8
        spacing = 8.0
        alignment = Pos.CENTER
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
                saveConfig(
                    isEnableProxy.get(),
                    taHeaders.text,
                    selectedProxy.get(),
                    tfIp.text,
                    tfPort.text
                )
                close()
            }
        }
    }
}
