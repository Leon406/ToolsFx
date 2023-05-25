package me.leon.mac

import java.security.Security
import kotlin.test.assertEquals
import me.leon.ext.crypto.mac
import me.leon.ext.crypto.macWithIv
import me.leon.ext.toHex
import org.bouncycastle.crypto.engines.ChaCha7539Engine
import org.bouncycastle.crypto.macs.Poly1305
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.junit.Test

class MacTest {
    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
    }

    @Test
    fun testHMac() {
        val data = "12341234".toByteArray()
        val key = "12341234".toByteArray()
        val expectedMap =
            mapOf(
                "KMAC128" to "65fb81f8fa7e68e0dd311cc2722c9b827c1942c82627df6ef3b3895e4f29970b",
                "KMAC256" to
                    "00a67d61530754405f8e775bd1b7bbf85aec19e47ff701bb320652eea4a939a703356fdfd0a7e61" +
                        "4c5edcfdfe0da1f6859170969e2ed922658373c84ba4649f4",
                "HmacMD5" to "bd4f49828cc8dd9521c95aae811da7e5",
                "HmacMD4" to "3cd7acb0a5140dbc8afad4ff0ca7b4ff",
                "HmacSM3" to "6d79919b6c41066b7807f0d49ec7f384b62ba49653586f7b8f5ca1aafc0fab2c",
                "HmacTiger" to "968ba5c4aa949390c002ab33021589a5c0ddcd00da84f361",
                "HmacSHA1" to "7a9d9667ddd8991c0a3e2b1207653b7648b0a8fa",
                "HmacSHA256" to "31f39befac99ab6d476110ff0eb66f19d46ac48022b05639fa71d2dea28b5bb1",
                "HmacSHA3-256" to
                    "29cc6b82d62c20f22f5a9b38ddffe622ac65801cf5aaf37448869590ffc16780",
                "HmacRIPEMD160" to "06e1f13f63cddb6f025521a9819a895250cb0c27",
                "HmacSkein-256-160" to "b699909587b8d02953619eaba027c3c735d574ff",
                "HmacKeccak224" to "d50b18b8a15d6d147c3f21564d8711d023ba8f5d000452c4b61afc34",
                "HmacDSTU7564-256" to
                    "5bbdd461892e5bf9e9c734f10a391df48f4336032a5e3686a89b2e43244f38ff",
                "HmacGOST3411" to
                    "374c2efa55f0d06fb355d93039f55949a9d0c7f50b35cb71d9e79b2d4aa5f3d1",
            )
        for ((name, expected) in expectedMap) assertEquals(expected, data.mac(key, name).toHex())
    }

    @Test
    fun testGMac() {
        val data = "12341234".toByteArray()
        val key = "1234123412341234".toByteArray()
        val iv = "12341234".toByteArray()
        val expectedMap =
            mapOf(
                "AES-GMAC" to "828fe5fb2ea276de9ecbee3c0ba3bdc9",
                "SM4-GMAC" to "052953c9ea57d3c23906d8219ebc6184",
                "RC6-GMAC" to "7e92152303b5dcb2dcd42d33de81c314",
                "CAST6-GMAC" to "80e0afa68887e6ea06ca55e8b984f2e4",
                "CAMELLIA-GMAC" to "5f852e5ebc1b5cba6298dc8a6c3361b9",
                "ARIA-GMAC" to "907b1cf09192109f168669da40733c37",
                "NOEKEON-GMAC" to "2c2e3856564a1f6ce64f887d88348cf5",
                "SEED-GMAC" to "0cde6cdfccf8e5018b1dc619e50b385c",
                "SERPENT-GMAC" to "807f13ff588c52583bbfbb18e1e3ba33",
                "Twofish-GMAC" to "caa68b88c2ae8b7c1d04079b42aef8f4",
            )

        for ((name, expected) in expectedMap) assertEquals(
            expected,
            data.macWithIv(key, iv, name).toHex()
        )
    }

    @Test
    fun testPoly1305Mac() {
        val data = "12341234".toByteArray()
        val key = "12341234123412341234123412341234".toByteArray()
        val iv = "1234123412341234".toByteArray()
        val expectedMap =
            mapOf(
                "POLY1305-AES" to "e4aaf0996c08b1af847fb302e59a4250",
                "POLY1305-ARIA" to "d0293b076a479d777f4d915103b39e95",
                "POLY1305-CAMELLIA" to "fdcf63a744dba6b213e017fde9e36de0",
                "POLY1305-CAST6" to "68c48cb920c913221aab897583b6680b",
                "POLY1305-NOEKEON" to "a59768cc2d11f21fc42b3f251c10017f",
                "POLY1305-RC6" to "9d9a13aeec4fc73927c24b2648a34625",
                "POLY1305-SEED" to "49844ea027f5cc63aa253a959b1cfc30",
                "POLY1305-SERPENT" to "b04e99702374185b5c751a4ea04784a4",
                "POLY1305-SM4" to "5b843372e2343c620987466d977db7a6",
                "POLY1305-Twofish" to "e3c00bc26e3d5fcf8dff3c0cb9ff30e9",
            )

        for ((name, expected) in expectedMap) assertEquals(
            expected,
            data.macWithIv(key, iv, name).toHex()
        )
    }

    @Test
    fun testCMac() {
        val data = "12341234".toByteArray()
        var key = "1234123412341234".toByteArray()
        val expectedMap =
            mapOf(
                "IDEAMAC" to "7d78bba0",
                "AESCMAC" to "580368b0e4e5510056ce164c58e3be1b",
                "SM4-CMAC" to "b4046eab133a887c94249740e4463a9c",
                "DESEDECMAC" to "ee0b9cd33cb6d298",
                "BLOWFISHCMAC" to "339e0dc83afcd195",
                "SEED-CMAC" to "0f385da442586a0f1faf832533a8685a",
                "Shacal-2CMAC" to
                    "1db2fede16d06e4761c8b975d471eb8a8ed5024a12e98799caf9b78ac0e1038a",
            )

        for ((name, expected) in expectedMap) assertEquals(expected, data.mac(key, name).toHex())

        key = "12341234".toByteArray()
        assertEquals("ee0b9cd33cb6d298", data.mac(key, "DESCMAC").toHex())
        key = "1234123412341234".toByteArray()
        var iv = "1234123412341234".toByteArray()
        data.macWithIv(key, iv, "ZUC-128").toHex().also { println(it) }
        key = "12341234123412341234123412341234".toByteArray()
        iv = "1234123412341234123412341".toByteArray()
        data.macWithIv(key, iv, "ZUC-256-32").toHex().also { println(it) }
    }

    @Test
    fun mac() {
        // 输入需要加密的数据和密钥
        val data = "Hello World!".toByteArray()
        val key = "ThisIsAKey123456ThisIsAKey123456".toByteArray()
        // 初始化ChaCha20加密引擎
        val chacha20 = ChaCha7539Engine()
        val parametersWithIV = ParametersWithIV(KeyParameter(key), ByteArray(12))
        chacha20.init(true, parametersWithIV)
        // 加密数据
        val encryptedData = ByteArray(data.size)
        chacha20.processBytes(data, 0, data.size, encryptedData, 0)

        // 初始化Poly1305消息认证码算法
        val poly1305 = Poly1305()
        poly1305.init(KeyParameter(key))
        // 计算消息认证码
        val mac = ByteArray(poly1305.macSize)
        poly1305.update(encryptedData, 0, encryptedData.size)
        poly1305.doFinal(mac, 0)
        // 输出加密后的数据和消息认证码
        println("Encrypted data: ${String(encryptedData)}")
        println("MAC: ${String(mac)}")
    }
}
