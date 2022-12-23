package me.leon.toolsfx.plugin.ext

interface SimpleService {

    fun process(file: String, params: Map<String, String>): Any
}
