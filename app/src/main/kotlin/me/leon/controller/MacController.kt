package me.leon.controller

import me.leon.DEBUG
import me.leon.ext.*
import me.leon.ext.crypto.mac
import me.leon.ext.crypto.macWithIv
import tornadofx.*

class MacController : Controller() {

    fun mac(
        msg: String,
        keyByteArray: ByteArray,
        alg: String,
        inputEncode: String,
        outputEncode: String,
        singleLine: Boolean = false
    ) =
        catch({ "mac error: $it" }) {
            if (DEBUG) println("mac $msg  $alg ")
            if (singleLine) {
                msg.lineAction2String {
                    it.decodeToByteArray(inputEncode).mac(keyByteArray, alg).encodeTo(outputEncode)
                }
            } else {
                msg.decodeToByteArray(inputEncode).mac(keyByteArray, alg).encodeTo(outputEncode)
            }
        }

    fun macWithIv(
        msg: String,
        keyByteArray: ByteArray,
        ivByteArray: ByteArray,
        alg: String,
        inputEncode: String,
        outputEncode: String,
        singleLine: Boolean = false
    ) =
        catch({ "mac error: $it" }) {
            if (singleLine) {
                msg.lineAction2String {
                    it.decodeToByteArray(inputEncode)
                        .macWithIv(keyByteArray, ivByteArray, alg)
                        .encodeTo(outputEncode)
                }
            } else {
                msg.decodeToByteArray(inputEncode)
                    .macWithIv(keyByteArray, ivByteArray, alg)
                    .encodeTo(outputEncode)
            }
        }
}
