package me.leon.toolsfx.plugin.ext

interface ImageService {

    fun process(raw: String, isFile: Boolean, params: Map<String, String>): Any
}
