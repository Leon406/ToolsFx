package me.leon.controller

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
        outEncode: String,
        isSingleLine: Boolean
    ) =
        catch({ it }) {
            if (isSingleLine)
                msg.lineAction2String {
                    it.decodeToByteArray(inputEncode).sign(kpAlg, sigAlg, pri).encodeTo(outEncode)
                }
            else msg.decodeToByteArray(inputEncode).sign(kpAlg, sigAlg, pri).encodeTo(outEncode)
        }

    fun verify(
        kpAlg: String,
        sigAlg: String,
        pub: String,
        msg: String,
        inputEncode: String,
        outEncode: String,
        signed: String,
        isSingleLine: Boolean
    ) =
        catch({ it }) {
            if (isSingleLine)
                msg.lineActionIndex { s, i ->
                    s.decodeToByteArray(inputEncode)
                        .verify(
                            kpAlg,
                            sigAlg,
                            pub,
                            signed.lineSplit()[i].decodeToByteArray(outEncode)
                        )
                        .toString()
                }
            else
                msg.decodeToByteArray(inputEncode)
                    .verify(kpAlg, sigAlg, pub, signed.decodeToByteArray(outEncode))
                    .toString()
        }
}
