package me.leon.controller

import me.leon.ext.*
import tornadofx.*

class ClassicalController : Controller() {

    fun encrypt(
        raw: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: MutableMap<String, String>,
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine) raw.lineAction2String { encrypt(it, type, params) }
        else encrypt(raw, type, params)

    fun encrypt(
        raw: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: MutableMap<String, String>
    ): String = catch({ "编码错误: $it" }) { if (raw.isEmpty()) "" else type.encrypt(raw, params) }

    fun decrypt(
        encoded: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: MutableMap<String, String>,
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine) encoded.lineAction2String { decrypt(it, type, params) }
        else decrypt(encoded, type, params)

    private fun decrypt(
        encrypted: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: MutableMap<String, String>,
    ) = type.decrypt(encrypted, params)
}
