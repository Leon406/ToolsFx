package me.leon.controller

import me.leon.encode.base.base64
import me.leon.ext.*
import org.bouncycastle.crypto.macs.KGMac
import tornadofx.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MacController : Controller() {
    fun mac(msg: String, hkey: String, alg: String, outputEncode: String) =
        catch({ "mac error: $it" }) {
            println("mac $msg  $alg $hkey")
            Mac.getInstance(alg)
                .apply {
                    init(SecretKeySpec(hkey.toByteArray(), alg))
                    update(msg.toByteArray())
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

    fun macWithIv(msg: String, key: String, iv: String, alg: String, outputEncode: String) =
        catch({ "mac error: $it" }) {
            println("mac $msg  $alg $key")
            val data = msg.toByteArray()
            val keyByteArray = key.toByteArray()
            val ivByteArray = iv.toByteArray()
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
