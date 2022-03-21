package me.leon.controller

import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.sign
import me.leon.ext.crypto.verify
import tornadofx.*

class SignatureController : Controller() {

    fun sign(
        kpAlg: String,
        sigAlg: String,
        pri: String,
        msg: String,
        inputEncode: String,
        isSingleLine: Boolean
    ) =
        catch({ "$it" }) {
            if (isSingleLine)
                msg.lineAction2String {
                    it.decodeToByteArray(inputEncode).sign(kpAlg, sigAlg, pri).base64()
                }
            else msg.decodeToByteArray(inputEncode).sign(kpAlg, sigAlg, pri).base64()
        }

    fun verify(
        kpAlg: String,
        sigAlg: String,
        pub: String,
        msg: String,
        inputEncode: String,
        signed: String,
        isSingleLine: Boolean
    ) =
        catch({ "$it" }) {
            if (isSingleLine)
                msg.lineActionIndex { s, i ->
                    s.decodeToByteArray(inputEncode)
                        .verify(kpAlg, sigAlg, pub, signed.lineSplit()[i].base64Decode())
                        .toString()
                }
            else
                msg.decodeToByteArray(inputEncode)
                    .verify(kpAlg, sigAlg, pub, signed.toByteArray())
                    .toString()
        }
}
