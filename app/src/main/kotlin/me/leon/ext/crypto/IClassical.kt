package me.leon.ext.crypto

interface IClassical {

    fun encrypt(raw: String, params: Map<String, String>): String

    fun decrypt(raw: String, params: Map<String, String>): String
    open fun crack(raw: String, keyword: String): String {
        return ""
    }

    fun paramsCount(): Int = 0

    fun paramsHints(): List<String> = listOf("", "")

    fun isIgnoreSpace(): Boolean = true
    fun hasCrack(): Boolean = false
}
