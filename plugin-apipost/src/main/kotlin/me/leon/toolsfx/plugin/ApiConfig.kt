package me.leon.toolsfx.plugin

import me.leon.ext.fx.Prefs
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import me.leon.toolsfx.plugin.net.NetHelper.parseHeaderString
import me.leon.toolsfx.plugin.net.NetHelper.proxyType

object ApiConfig {
    private const val IS_ENABLE_PROXY = "isEnableProxy"
    private const val GLOBAL_HEADERS = "globalHeaders"
    private const val PROXY_TYPE = "proxyType"
    private const val PROXY_HOST = "proxyHost"
    private const val PROXY_PORT = "proxyPort"
    private const val TIME_OUT = "timeout"
    var isEnableProxy
        get() = Prefs.preference().getBoolean(IS_ENABLE_PROXY, false)
        set(value) {
            Prefs.preference().putBoolean(IS_ENABLE_PROXY, value)
        }

    var globalHeaders: String
        get() = Prefs.preference().get(GLOBAL_HEADERS, "")
        set(value) {
            Prefs.preference().put(GLOBAL_HEADERS, value)
        }
    var proxyType: String
        get() = Prefs.preference().get(PROXY_TYPE, "HTTP")
        set(value) {
            Prefs.preference().put(PROXY_TYPE, value)
        }
    var proxyHost: String
        get() = Prefs.preference().get(PROXY_HOST, "127.0.0.1")
        set(value) {
            Prefs.preference().put(PROXY_HOST, value)
        }
    var proxyPort: String
        get() = Prefs.preference().get(PROXY_PORT, "80")
        set(value) {
            Prefs.preference().put(PROXY_PORT, value)
        }

    var timeOut: Int
        get() = Prefs.preference().getInt(TIME_OUT, 10_000)
        set(value) {
            Prefs.preference().putInt(TIME_OUT, value)
        }

    fun resortFromConfig() {
        if (isEnableProxy) {
            HttpUrlUtil.setupProxy(proxyType.proxyType(), proxyHost, proxyPort.toInt())
        }
        HttpUrlUtil.globalHeaders.putAll(
            parseHeaderString(globalHeaders) as MutableMap<String, String>
        )
        HttpUrlUtil.timeOut = timeOut
    }

    fun saveConfig(
        isEnablePro: Boolean,
        headers: String,
        pType: String,
        pHost: String,
        pPort: String,
        tOut: Int,
    ) {
        isEnableProxy = isEnablePro
        if (isEnableProxy) {
            HttpUrlUtil.setupProxy(proxyType.proxyType(), proxyHost, proxyPort.toInt())
        } else HttpUrlUtil.setupProxy()
        val previousHeaders: MutableMap<String, String> =
            parseHeaderString(globalHeaders) as MutableMap<String, String>
        HttpUrlUtil.globalHeaders - previousHeaders.keys
        globalHeaders = headers
        HttpUrlUtil.globalHeaders.putAll(
            parseHeaderString(globalHeaders) as MutableMap<String, String>
        )
        proxyType = pType
        proxyHost = pHost
        proxyPort = pPort
        HttpUrlUtil.timeOut = tOut
        timeOut = tOut
    }
}
