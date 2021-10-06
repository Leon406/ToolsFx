package me.leon.toolsfx.plugin.net

data class Request(
    var url: String,
    var method: String = "GET",
    val params: MutableMap<String, Any> = mutableMapOf(),
    val headers: MutableMap<String, Any> = mutableMapOf(),
)
