package me.leon.view

interface MiscService {

    fun process(raw: String, params: MutableMap<String, String>): String

    fun hint(): String
}
