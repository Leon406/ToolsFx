package me.leon.ext.crypto

import org.bouncycastle.crypto.*
import org.bouncycastle.crypto.engines.*
import org.bouncycastle.crypto.macs.GMac
import org.bouncycastle.crypto.macs.KGMac
import org.bouncycastle.crypto.modes.GCMBlockCipher
import org.bouncycastle.crypto.modes.KGCMBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV

object GMac {
    fun getInstance(alg: String): Mac {
        val blockCipher: BlockCipher =
            when (alg.uppercase()) {
                in arrayOf("SERPENT-GMAC", "TNEPRES-GMAC") -> TnepresEngine()
                "TWOFISH-GMAC" -> TwofishEngine()
                "ARIA-GMAC" -> ARIAEngine()
                "SEED-GMAC" -> SEEDEngine()
                "AES-GMAC" -> AESEngine()
                "SM4-GMAC" -> SM4Engine()
                "CAMELLIA-GMAC" -> CamelliaEngine()
                "NOEKEON-GMAC" -> NoekeonEngine()
                "CAST6-GMAC" -> CAST6Engine()
                "RC6-GMAC" -> RC6Engine()
                in arrayOf("DSTU7624-128GMAC", "DSTU7624GMAC") -> DSTU7624Engine(128)
                "DSTU7624-256GMAC" -> DSTU7624Engine(256)
                "DSTU7624-512GMAC" -> DSTU7624Engine(512)
                else -> throw IllegalArgumentException("illegal alg")
            }

        return if (alg.contains("DSTU7624", true)) {
            KGMac(KGCMBlockCipher(blockCipher))
        } else {
            GMac(GCMBlockCipher(blockCipher))
        }
    }
}

fun Mac.macWithIv(key: ByteArray, iv: ByteArray, data: ByteArray): ByteArray {
    val params: CipherParameters = ParametersWithIV(KeyParameter(key), iv)
    this.init(params)
    update(data, 0, data.size)
    val sig = ByteArray(macSize)
    doFinal(sig, 0)
    return sig
}
