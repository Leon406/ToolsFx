package me.leon.toolsfx.plugin

import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.toolsfx.plugin.compress.Compression
import tornadofx.*

class CompressController : Controller() {
    fun compress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
        isSingleLine: Boolean = false,
    ): String =
        if (isSingleLine) raw.lineAction2String { compress(it, alg, inputEncode, outputEncode) }
        else compress(raw, alg, inputEncode, outputEncode)

    private fun compress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String =
        catch({ "encrypt error: $it" }) {
            println("encrypt  $alg")
            val inputBytes =
                when (inputEncode) {
                    "raw" -> raw.toByteArray()
                    "base64" -> raw.base64Decode()
                    "hex" -> raw.hex2ByteArray()
                    else -> throw IllegalArgumentException("input encode error")
                }

            alg.compress(inputBytes).run {
                when (outputEncode) {
                    "raw" -> this.decodeToString()
                    "base64" -> this.base64()
                    "hex" -> this.toHex()
                    else -> throw IllegalArgumentException("input encode error")
                }
            }
        }

    fun decompress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
        isSingleLine: Boolean = false,
    ): String =
        if (isSingleLine) raw.lineAction2String { decompress(it, alg, inputEncode, outputEncode) }
        else decompress(raw, alg, inputEncode, outputEncode)

    private fun decompress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ) =
        catch({ "decrypt error: $it" }) {
            println("decrypt  $alg")
            val inputBytes =
                when (inputEncode) {
                    "raw" -> raw.toByteArray()
                    "base64" -> raw.base64Decode()
                    "hex" -> raw.hex2ByteArray()
                    else -> throw IllegalArgumentException("input encode error")
                }

            alg.decompress(inputBytes).run {
                when (outputEncode) {
                    "raw" -> this.decodeToString()
                    "base64" -> this.base64()
                    "hex" -> this.toHex()
                    else -> throw IllegalArgumentException("input encode error")
                }
            }
        }
}
