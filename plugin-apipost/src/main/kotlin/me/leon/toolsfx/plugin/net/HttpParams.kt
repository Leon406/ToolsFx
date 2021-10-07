package me.leon.toolsfx.plugin.net

data class HttpParams @JvmOverloads constructor(var key: String = "", var value: String ="") {
    var isFile: Boolean = false
    var isEnable: Boolean = true
}
