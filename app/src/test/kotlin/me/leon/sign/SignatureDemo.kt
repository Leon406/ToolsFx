package me.leon.sign

import java.math.BigInteger
import java.security.KeyPair
import java.security.Security
import me.leon.*
import me.leon.encode.base.base64
import me.leon.encode.base.base64UrlDecode2String
import me.leon.ext.crypto.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.UrlBase64

/** @link https://www.liaoxuefeng.com/wiki/1252599548343744/1304227943022626 */
object SignatureDemo {
    init {
        Security.addProvider(BouncyCastleProvider())
        Security.getProviders()
            .filter { it.name == "BC" }
            .flatMap { it.services }
            //            .filter { it.type in listOf("Signature", "KeyPairGenerator") }
            .groupBy { it.type }
            .forEach { (k, v) ->
                println("$k Algorithm: \n\t${v.joinToString("\n\t") { it.algorithm }}")
            }
    }

    private fun signShhincsPlusCheck() {
        val keyPairAlg = "SPHINCSPLUS"
        val sigAlg = "SPHINCSPLUS"
        repeat(sphincsPlusList.count()) {
            val kp = genKeyPair(keyPairAlg, listOf(sphincsPlusList[it]))
            signTest(kp, sigAlg, keyPairAlg)
        }
    }

    private fun signLmsCheck() {
        val keyPairAlg = "LMS"
        val sigAlg = "LMS"

        for (sig in lmsSigList) for (param in lmsParamsList) {
            val kp = genKeyPair(keyPairAlg, listOf(sig, param))
            signTest(kp, sigAlg, keyPairAlg)
        }
    }

    private fun signTest(kp: KeyPair, sigAlg: String, keyPairAlg: String) {
        val sk = kp.private
        val pk = kp.public
        val privateKey = sk.encoded.base64()
        val pubKey = pk.encoded.base64()
        println("公钥: $pubKey \n私钥: $privateKey")
        val message = "Hello, I am Bob!".toByteArray()
        // 用私钥签名:
        val signed = message.sign(keyPairAlg, sigAlg, privateKey)
        println(String.format("signature: %x", BigInteger(1, signed)))

        // 用公钥验证:
        val valid = message.verify(keyPairAlg, sigAlg, pubKey, signed)
        println("valid? $valid")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(UrlBase64.encode("1234".toByteArray()).decodeToString().base64UrlDecode2String())
        signShhincsPlusCheck()
        signLmsCheck()
        normal()
    }

    private fun normal() {

        // 公私钥  keysize   MAC算法
        // RSA   1024     SHA512withRSA
        // DSA  512 (最小)     SHA512withDSA
        // ECDSA  224     SHA512withECDSA
        val keyPairAlg = "RSA"
        val keySize = 1024

        //    val sigAlg = "SHA1withRSA"
        val sigAlg = "SHA1withRSA"
        // 生成公钥/私钥:
        val kp = genKeyPair(keyPairAlg, listOf(keySize))
        signTest(kp, sigAlg, keyPairAlg)
    }
}
