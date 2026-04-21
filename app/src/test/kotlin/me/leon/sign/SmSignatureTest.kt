package me.leon.sign

import kotlin.test.Test
import me.leon.ext.crypto.sm2Sign
import me.leon.ext.crypto.sm2Verify
import me.leon.ext.toHex
import kotlin.test.assertTrue

class SmSignatureTest {
    val pub =
        "042b515d98e00cd3f5770e20ba69998da6f9637085a0c89dec0a5dec7771fa4d0569a68a3d" +
                "eba36ee5ffd89391e62d775551f6d69de7d155f0a3fdcbacb0631ea9"
    val pri = "7ac2d094b66dbde1170875190bca025ed38c8293dabad39e00692318ca624e60"

    @Test
    fun smSignatureTest() {
        val text = "Hello, SM2!"
        val sign = text.toByteArray().sm2Sign(pri).toHex()
        assertTrue(text.toByteArray().sm2Verify(pub, sign))
    }
}
