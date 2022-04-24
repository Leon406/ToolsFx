package me.leon.dh

import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import kotlin.test.assertEquals
import me.leon.encode.base.base64
import me.leon.ext.crypto.genKeyPair
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

class DHTest {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun dh() {
        // 512~2048 , 64的倍数
        val keySize = 2048
        val alg = "DH"
        val alice = genKeyPair(alg, listOf(keySize))
        val bob = genKeyPair(alg, listOf(keySize))

        val aliceKey = alice.generateSecretKey(bob.public.encoded)?.base64().also { println(it) }

        val bobKey = bob.generateSecretKey(alice.public.encoded)?.base64().also { println(it) }
        assertEquals(aliceKey, bobKey)
    }

    fun KeyPair.generateSecretKey(receivedPubKeyBytes: ByteArray, alg: String = "DH"): ByteArray? =
        runCatching {
                val keySpec = X509EncodedKeySpec(receivedPubKeyBytes)
                val kf = KeyFactory.getInstance(alg, "BC")
                val receivedPublicKey: PublicKey = kf.generatePublic(keySpec)
                // 生成本地密钥:
                val keyAgreement = KeyAgreement.getInstance(alg, "BC")
                keyAgreement.init(private) // 自己的PrivateKey
                keyAgreement.doPhase(receivedPublicKey, true) // 对方的PublicKey
                // 生成SecretKey密钥:
                keyAgreement.generateSecret()
            }
            .getOrNull()
}
