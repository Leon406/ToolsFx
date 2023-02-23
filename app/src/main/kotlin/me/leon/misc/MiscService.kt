package me.leon.misc

interface MiscService {

    fun process(raw: String, params: MutableMap<String, String>): String

    fun hint(): String
}
