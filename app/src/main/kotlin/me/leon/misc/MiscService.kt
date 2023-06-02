package me.leon.misc

interface MiscService {

    fun process(raw: String, params: Map<String, String>): String

    fun hint(): String

    fun paramsHints(): Array<out String>

    fun options(): Array<out String>
}
