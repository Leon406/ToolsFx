package me.leon.controller

import me.leon.ext.catch
import me.leon.ext.crypto.ClassicalCryptoType
import me.leon.ext.lineAction2String
import tornadofx.*

class ClassicalController : Controller() {

    fun encrypt(
        raw: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: Map<String, String>,
        singleLine: Boolean = false
    ) =
        catch({ "编码错误: $it" }) {
            if (singleLine) {
                raw.lineAction2String { encrypt(it, type, params) }
            } else {
                encrypt(raw, type, params)
            }
        }

    private fun encrypt(
        raw: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: Map<String, String>
    ): String =
        if (raw.isEmpty()) {
            ""
        } else {
            type.encrypt(raw, params)
        }

    fun decrypt(
        encoded: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        params: Map<String, String>,
        singleLine: Boolean = false
    ) =
        catch({ "解密错误: $it" }) {
            if (singleLine) {
                encoded.lineAction2String { type.decrypt(it, params) }
            } else {
                type.decrypt(encoded, params)
            }
        }

    fun crack(
        encoded: String,
        type: ClassicalCryptoType = ClassicalCryptoType.CAESAR,
        keyword: String,
        singleLine: Boolean = false,
        params: Map<String, String>
    ) =
        catch({ "解密错误: $it" }) {
            if (singleLine) {
                encoded.lineAction2String { type.crack(it, keyword, params) }
            } else {
                type.crack(encoded, keyword, params)
            }
        }
}
