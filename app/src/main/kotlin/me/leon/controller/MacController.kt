package me.leon.controller

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import me.leon.encode.base.base64
import me.leon.ext.*
import org.bouncycastle.crypto.macs.KGMac
import tornadofx.*

class MacController : Controller() {

    fun mac(
        msg: String,
        keyByteArray: ByteArray,
        alg: String,
        inputEncode: String,
        outputEncode: String,
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine)
            msg.lineAction2String { mac(it, keyByteArray, alg, inputEncode, outputEncode) }
        else mac(msg, keyByteArray, alg, inputEncode, outputEncode)

    private fun mac(
        msg: String,
        keyByteArray: ByteArray,
        alg: String,
        inputEncode: String,
        outputEncode: String
    ) =
        catch({ "mac error: $it" }) {
            println("mac $msg  $alg ")
            Mac.getInstance(alg)
                .apply {
                    init(SecretKeySpec(keyByteArray, alg))
                    update(msg.decodeToByteArray(inputEncode))
                }
                .doFinal()
                .run {
                    if (outputEncode == "hex") {
                        this.toHex()
                    } else {
                        this.base64()
                    }
                }
        }

    fun macWithIv(
        msg: String,
        keyByteArray: ByteArray,
        ivByteArray: ByteArray,
        alg: String,
        inputEncode: String,
        outputEncode: String,
        isSingleLine: Boolean = false
    ) =
        if (isSingleLine)
            msg.lineAction2String {
                macWithIv(it, keyByteArray, ivByteArray, alg, inputEncode, outputEncode)
            }
        else macWithIv(msg, keyByteArray, ivByteArray, alg, inputEncode, outputEncode)

    private fun macWithIv(
        msg: String,
        keyByteArray: ByteArray,
        ivByteArray: ByteArray,
        alg: String,
        inputEncode: String,
        outputEncode: String
    ) =
        catch({ "mac error: $it" }) {
            println("mac $msg  $alg")
            val data = msg.decodeToByteArray(inputEncode)
            if (alg.contains("POLY1305")) {
                Poly1305Serial.getInstance(alg).run {
                    init(keyByteArray, ivByteArray)
                    update(data, 0, data.size)
                    val sig = ByteArray(macSize)
                    doFinal(sig, 0)
                    if (outputEncode == "hex") sig.toHex() else sig.base64()
                }
            } else if (alg.contains("GMAC")) {
                GMac.getInstance(alg).run {
                    if (this is org.bouncycastle.crypto.macs.GMac) {
                        init(keyByteArray, ivByteArray)
                        update(data, 0, data.size)
                        val sig = ByteArray(macSize)
                        doFinal(sig, 0)
                        if (outputEncode == "hex") sig.toHex() else sig.base64()
                    } else if (this is KGMac) {
                        init(keyByteArray, ivByteArray)
                        update(data, 0, data.size)
                        val sig = ByteArray(macSize)
                        doFinal(sig, 0)
                        if (outputEncode == "hex") sig.toHex() else sig.base64()
                    } else {
                        ""
                    }
                }
            } else {
                ""
            }
        }
}
