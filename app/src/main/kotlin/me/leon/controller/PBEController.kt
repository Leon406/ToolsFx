package me.leon.controller

import me.leon.DEBUG
import me.leon.ext.catch
import me.leon.ext.crypto.PBE
import me.leon.ext.lineAction2String
import tornadofx.*

class PBEController : Controller() {
    fun encrypt(
        password: String,
        data: String,
        salt: ByteArray,
        alg: String,
        iteration: Int,
        keyLength: Int,
        singleLine: Boolean
    ) =
        catch({ "encrypt error: $it" }) {
            if (DEBUG) println("encrypt  $alg $data")
            if (singleLine) {
                data.lineAction2String {
                    PBE.encrypt(password, it, salt, alg, iteration, keyLength)
                }
            } else {
                PBE.encrypt(password, data, salt, alg, iteration, keyLength)
            }
        }

    fun decrypt(
        password: String,
        data: String,
        saltLength: Int,
        alg: String,
        iteration: Int,
        keyLength: Int,
        singleLine: Boolean
    ) =
        catch({ "decrypt error: $it" }) {
            if (DEBUG) println("decrypt  $alg $data")
            if (singleLine) {
                data.lineAction2String {
                    PBE.decrypt(password, it, saltLength, alg, iteration, keyLength)
                }
            } else {
                PBE.decrypt(password, data, saltLength, alg, iteration, keyLength)
            }
        }

    fun getSalt(length: Int) = PBE.getSalt(length)
}
