package me.leon.ext.crypto

interface IClassical {

    fun encrypt(raw: String, params: Map<String, String>): String

    fun decrypt(raw: String, params: Map<String, String>): String

    fun paramsCount(): Int = 0

    fun paramsHints(): List<String> = listOf("", "")

    fun isIgnoreSpace(): Boolean = true
}
