package me.leon.plugin

data class Request(
    var url: String,
    var method: String,
    val params: MutableMap<String, Any>,
    val headers: MutableMap<String, Any>,
)
