package me.leon

data class Api(
    val api: String,
    val file: String,
    val headers: LinkedHashMap<String, String>,
    val body: LinkedHashMap<String, String>,
    val result: String
)
