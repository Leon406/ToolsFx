package me.leon.toolsfx.plugin

import me.leon.ext.*
import me.leon.toolsfx.plugin.compress.Compression
import tornadofx.*

class CompressController : Controller() {
    fun compress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
        singleLine: Boolean = false,
    ): String =
        catch({ "encrypt error: $it" }) {
            println("encrypt  $alg")
            if (singleLine) {
                raw.lineAction2String { compress(it, alg, inputEncode, outputEncode) }
            } else {
                compress(raw, alg, inputEncode, outputEncode)
            }
        }

    private fun compress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String = alg.compress(raw.decodeToByteArray(inputEncode)).encodeTo(outputEncode)

    fun decompress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
        singleLine: Boolean = false,
    ): String =
        catch({ "decrypt error: $it" }) {
            println("decrypt  $alg")
            if (singleLine) {
                raw.lineAction2String { decompress(it, alg, inputEncode, outputEncode) }
            } else {
                decompress(raw, alg, inputEncode, outputEncode)
            }
        }

    private fun decompress(
        raw: String,
        alg: Compression = Compression.GZIP,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ) = alg.decompress(raw.decodeToByteArray(inputEncode)).encodeTo(outputEncode)
}
