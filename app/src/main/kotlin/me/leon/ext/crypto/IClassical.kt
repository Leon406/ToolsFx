package me.leon.ext.crypto

@Suppress("ALL")
interface IClassical {

    fun encrypt(raw: String, params: Map<String, String>): String

    fun decrypt(raw: String, params: Map<String, String>): String

    fun crack(raw: String, keyword: String): String {
        return ""
    }

    // for compatible
    fun crack(raw: String, keyword: String, params: Map<String, String>): String {
        return crack(raw, keyword)
    }

    fun paramsHints(): Array<out String>

    fun checkboxHints(): Array<out String>

    fun paramsCount(): Int = paramsHints().size

    fun checkboxHintsCount(): Int = checkboxHints().size

    fun isIgnoreSpace(): Boolean = true

    fun hasCrack(): Boolean = false
}
