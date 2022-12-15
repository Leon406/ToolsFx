package me.leon.toolsfx.plugin.sample2

interface Sample2Service {

    fun process(raw: String, params: MutableMap<String, String>): String
}
