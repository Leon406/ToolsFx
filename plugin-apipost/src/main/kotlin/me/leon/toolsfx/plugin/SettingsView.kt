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
    private val enableProxy = SimpleBooleanProperty(ApiConfig.isEnableProxy)
    private val followRedirect = SimpleBooleanProperty(ApiConfig.followRedirect)
    private val ignoreCert = SimpleBooleanProperty(ApiConfig.isIgnoreCert)
    private val p12 = SimpleBooleanProperty(false)

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

        hbox {
            label("FollowRedirect")
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            checkbox("", followRedirect)
        }

        label("Certification")
        hbox {
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            checkbox("p12", p12)
            tfCerPath = textfield {
                promptText = "file path(drag file here)"
                onDragEntered = eventHandler
            }

            tfCerPass = textfield {
                enableWhen(p12)
                promptText = "pkcs12 password"
            }
            checkbox("ignoreSSL", ignoreCert)
        }
        label("Proxy")
        hbox {
            spacing = 8.0
            alignment = Pos.CENTER
            combobox(selectedProxy, proxies)
            tfIp =
                textfield(ApiConfig.proxyHost) {
                    enableWhen(enableProxy)
                    promptText = "ip"
                }
            tfPort =
                textfield(ApiConfig.proxyPort) {
                    enableWhen(enableProxy)
                    promptText = "port"
                }
            checkbox("enable", enableProxy)
        }
        label("Global Headers")
        taHeaders = textarea(ApiConfig.globalHeaders) { promptText = "global header" }

        button("apply") {
            action {
                if (tfCerPath.text.isNotEmpty()) {
                    if (p12.get()) {
                        TrustManager.parseFromPkcs12(tfCerPath.text, tfCerPass.text)
                    } else {
                        TrustManager.parseFromCertification(tfCerPath.text)
                    }
                }
                saveConfig(
                    enableProxy.get(),
                    taHeaders.text,
                    selectedProxy.get(),
                    tfIp.text,
                    tfPort.text,
                    tfTime.text.toInt(),
                    followRedirect.get(),
                    ignoreCert.get()
                )
                close()
            }
        }
    }
}
