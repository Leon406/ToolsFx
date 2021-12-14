package me.leon.toolsfx.plugin

interface ILocationService {

    fun process(raw: String, params: MutableMap<String, String>): String
}
