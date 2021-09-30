package me.leon.plugin

data class Response(
    val code: Int,
    val data: String,
    val headers: MutableMap<String, Any>,
    val time: Long
)
