package me.leon.asymmetric

import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.SmUtil
import cn.hutool.crypto.asymmetric.KeyType
import java.security.*
import me.leon.encode.base.base64
import org.bouncycastle.crypto.params.*
import org.junit.Test

class Sm2Test {

    @Test
    fun hutool() {
        val text = "我是一段测试aaaa"

        val pair = SecureUtil.generateKeyPair("SM2")
        val privateKey = pair.private.encoded
        val publicKey = pair.public.encoded

        println(privateKey.base64())
        println(publicKey.base64())

        val sm2 = SmUtil.sm2(privateKey, publicKey)
        val encryptStr = sm2.encryptBcd(text, KeyType.PublicKey)
        println(encryptStr)
        val decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey))
        println(decryptStr)
    }
}
