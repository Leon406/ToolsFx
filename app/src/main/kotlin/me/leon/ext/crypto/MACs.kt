package me.leon.ext.crypto

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.crypto.macs.KGMac

object MACs {
    // https://www.bouncycastle.org/specifications.html
    val algorithm =
        linkedMapOf(
            "HmacMD5" to listOf("128"),
            "HmacMD4" to listOf("128"),
            "HmacMD2" to listOf("128"),
            "HmacSM3" to listOf("256"),
            "HmacTiger" to listOf("192"),
            "HmacWhirlpool" to listOf("512"),
            "HmacSHA1" to listOf("160"),
            "HmacSHA2" to listOf("224", "256", "384", "512", "512/224", "512/256"),
            "HmacSHA3" to listOf("224", "256", "384", "512"),
            "HmacRIPEMD" to listOf("128", "160", "256", "320"),
            "HmacKeccak" to listOf("224", "256", "288", "384", "512"),
            "HmacDSTU7564" to listOf("256", "384", "512"),
            "SIPHASH" to listOf("2-4", "4-8"),
            "SIPHASH128" to listOf("2-4", "4-8"),
            "HmacDSTU7564" to listOf("256", "384", "512"),
            "HmacSkein" to
                listOf(
                    "256-160",
                    "256-224",
                    "256-256",
                    "512-128",
                    "512-160",
                    "512-224",
                    "512-256",
                    "512-384",
                    "512-512",
                    "1024-384",
                    "1024-512",
                    "1024-1024"
                ),
            "HmacGOST3411" to listOf("256"),
            "HmacGOST3411-2012" to listOf("256", "512"),
            "POLY1305" to
                listOf(
                    "AES",
                    "ARIA",
                    "CAMELLIA",
                    "CAST6",
                    "NOEKEON",
                    "RC6",
                    "SEED",
                    "SERPENT",
                    "SM4",
                    "Twofish"
                ),
            "GMAC" to
                listOf(
                    "AES",
                    "ARIA",
                    "CAMELLIA",
                    "CAST6",
                    "NOEKEON",
                    "RC6",
                    "SEED",
                    "SERPENT",
                    "SM4",
                    "Twofish"
                ),
            "AESCMAC" to listOf("256"),
            "BLOWFISHCMAC" to listOf("256"),
            "DESCMAC" to listOf("256"),
            "DESEDECMAC" to listOf("256"),
            "SEED-CMAC" to listOf("256"),
            "Shacal-2CMAC" to listOf("256"),
            "SM4-CMAC" to listOf("256"),
            "Threefish" to listOf("256CMAC", "512CMAC", "1024CMAC"),
        )

    fun mac(data: ByteArray, keyByteArray: ByteArray, alg: String) =
        Mac.getInstance(alg)
            .apply {
                init(SecretKeySpec(keyByteArray, alg))
                update(data)
            }
            .doFinal()

    fun macWithIv(data: ByteArray, keyByteArray: ByteArray, ivByteArray: ByteArray, alg: String) =
        if (alg.contains("POLY1305")) {
            Poly1305Serial.getInstance(alg).run {
                init(keyByteArray, ivByteArray)
                update(data, 0, data.size)
                val sig = ByteArray(macSize)
                doFinal(sig, 0)
                sig
            }
        } else if (alg.contains("GMAC")) {
            GMac.getInstance(alg).run {
                if (this is org.bouncycastle.crypto.macs.GMac) {
                    init(keyByteArray, ivByteArray)
                    update(data, 0, data.size)
                    val sig = ByteArray(macSize)
                    doFinal(sig, 0)
                    sig
                } else if (this is KGMac) {
                    init(keyByteArray, ivByteArray)
                    update(data, 0, data.size)
                    val sig = ByteArray(macSize)
                    doFinal(sig, 0)
                    sig
                } else {
                    byteArrayOf()
                }
            }
        } else {
            byteArrayOf()
        }
}

fun ByteArray.mac(keyByteArray: ByteArray, alg: String) = MACs.mac(this, keyByteArray, alg)

fun ByteArray.macWithIv(keyByteArray: ByteArray, ivByteArray: ByteArray, alg: String) =
    MACs.macWithIv(this, keyByteArray, ivByteArray, alg)
