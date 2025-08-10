package me.leon.toolsfx.plugin

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.layout.Priority
import me.leon.IMG_IMPORT
import me.leon.ext.fx.clipboardText
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

    private lateinit var taHeaders: TextInputControl
    private lateinit var tfIp: TextField
    private lateinit var tfPort: TextField
    private lateinit var tfUserName: TextField
    private lateinit var tfPassword: TextField
    private lateinit var tfTime: TextField
    private lateinit var tfCurlDir: TextField
    private lateinit var tfCerPath: TextField
    private lateinit var tfCerPass: TextField
    private val eventHandler = fileDraggedHandler {
        with(it.first()) { tfCerPath.text = absolutePath }
    }
    private val curlEventHandler = fileDraggedHandler {
        with(it.first()) { tfCurlDir.text = absolutePath }
    }
    override val root = vbox {
        paddingAll = 8
        spacing = 8.0
        alignment = Pos.CENTER

        hbox {
            label("TimeOut")
            tfTime = textfield(ApiConfig.timeOut.toString()) { promptText = "Time in millisecond" }
            label("FollowRedirect")
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            checkbox("", followRedirect)
        }
        hbox {
            label("Curl File Dir")
            tfCurlDir =
                textfield(ApiConfig.curlDir) {
                    promptText = "File Path of Your curl"
                    onDragEntered = curlEventHandler
                }
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
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
        hbox {
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            label("Proxy")
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action {
                    val proxyInfos = clipboardText().split("@").map { it.split(":") }
                    runCatching {
                        if (proxyInfos.size == 2) {
                            tfIp.text = proxyInfos[1][0]
                            tfPort.text = proxyInfos[1][1]
                            tfUserName.text = proxyInfos[0][0]
                            tfPassword.text = proxyInfos[0][1]
                        } else {
                            tfIp.text = proxyInfos[0][0]
                            tfPort.text = proxyInfos[0][1]
                            tfUserName.text = null
                            tfPassword.text = null
                        }
                    }
                }
            }
        }

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
        hbox {
            spacing = 8.0
            paddingLeft = 24.0
            alignment = Pos.CENTER
            tfUserName =
                textfield(ApiConfig.proxyUser) {
                    enableWhen(enableProxy)
                    promptText = "name, optional"
                }
            tfPassword =
                textfield(ApiConfig.proxyPassword) {
                    enableWhen(enableProxy)
                    promptText = "password,optional"
                }
        }
        label("Global Headers")
        taHeaders =
            textarea(ApiConfig.globalHeaders) {
                vgrow = Priority.ALWAYS
                isWrapText = true
                promptText = "global header"
            }

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
                    tfUserName.text,
                    tfPassword.text,
                    tfTime.text.toInt(),
                    followRedirect.get(),
                    ignoreCert.get(),
                    tfCurlDir.text,
                )
                close()
            }
        }
    }
}
