package me.leon.hash

import java.io.File
import java.security.Security
import java.util.zip.CRC32
import kotlin.test.assertEquals
import me.leon.*
import me.leon.controller.DigestController
import me.leon.ext.crypto.*
import me.leon.ext.toHex
import org.junit.Ignore
import org.junit.Test

class HashTest {
    private val expectedMap =
        mapOf(
            "MD5" to "25d55ad283aa400af464c76d713c07ad",
            "MD4" to "012d73e0fab8d26e0f4d65e36077511e",
            "MD2" to "db5d888a0480461f4fb978746d1baf34",
            "SM3" to "0fffff81e971fa3f09107abf77931463fc0710bfb8962efeae3d5654b073bb0c",
            "Tiger" to "c603366663e8bef9e32a17b54ffb4a872858536165509dd5",
            "Whirlpool" to
                "8c9ec9f0ac6ad6fea526dad1171e7507262d0ea9541a2e6ddcc5767a239d1da9edea9" +
                    "48dbde5494bb7fb27df24d6ac5dfc3ad47cc52af76a7e9146c5125c6cf5",
            "SHA1" to "7c222fb2927d828af22f592134e8932480637c0d",
            "SHA-224" to "7e6a4309ddf6e8866679f61ace4f621b0e3455ebac2e831a60f13cd1",
            "SHA-256" to "fed9efbd5a8ef6820d639dbcb831daf9d6308312cc73d6188beb54a9a148e29a",
            "SHA-384" to
                "8cafed2235386cc5855e75f0d34f103ccc183912e5f02446b77c66539f776e4bf" +
                    "2bf87339b4518a7cb1c2441c568b0f8",
            "SHA-512" to
                "6cbce8f347e8d1b3d3517b27fdc4ee1c71d8406ab54e2335f3a39732fa0009d22" +
                    "193c41677d18504e90b4c1138c32e7cc1aa7500597ba99cacd525ef2c44e9dc",
            "SHA-512/224" to "505528b5ed4b207bfbe32de9a412a9232e1fba638fce3c67f3ee57a2",
            "SHA-512/256" to "2f1ca134776aba568b13fafc8d94bfbafc8b919d8d300892c88b90de5f492050",
            "SHA3-224" to "64f977868a7a63bb65ddcd10c587b50ad21fe885fe667c99acc5d6f1",
            "SHA3-256" to "c4b13691620d52a13b08494019d4a22c5d50d73ba453adcb2a9daffe64940723",
            "SHA3-384" to
                "9144284a23a97899c1031a1f4f6c1b4c86b156ef015ccf8704c2a54496c364dfab" +
                    "9fc02750617c354aa25cb60c035b4e",
            "SHA3-512" to
                "b9617cf40b229d1701e16b96deaf2911d5ce158751affb8670436c88d05da8872bbf" +
                    "51435d999f60ec5b82a5f45d5e61863f7b13a7ff7a2fa9949d06fe838df5",
            "RIPEMD128" to "953928d721b9b6692a7f15bd4d7e8010",
            "RIPEMD160" to "d63fe6f190c15627233789931371e6246e2f39ee",
            "RIPEMD256" to "7d792e853f8f174b964a94d532afc16d348e6e5f2b2035aed94318e884f32ab5",
            "RIPEMD320" to
                "29835ff1252e46388b580035770bd5a738d1a0f64169104bdf29dec44e7313a54a67a2d6357daa1f",
            "Keccak-224" to "ec7da48931748c1a0d202a41cd05abe345cebd545d56ae0f8c463fba",
            "Keccak-256" to "346367d081d5ef7f462e5b2e2270b1af268cc0223b37b6d2198c3149a3c37d36",
            "Keccak-288" to
                "b9413ebd933c93e4ab469d72200d04300ca3907647a9e22430b2e385ae3338e17c5ef329",
            "Keccak-384" to
                "9954ac5a81f6192ea07b91bf790cce46b3bb96805f3a3b9826855968fc28c8c6b" +
                    "bdfc2067593fa6277f72720aa137abb",
            "Keccak-512" to
                "f00511b126e7d99db139cc6cde95e0d809c1264bcd41f6a56b5e8ace15b86024a" +
                    "49d414343a90ceeada9d9b81a74b4212c1fc4945a7a3a764d49ddad7942bff3",
            "Blake2b-160" to "bc097fa99ff59963cecb5f350c2bd89eae64adf6",
            "Blake2b-256" to "8256da79b69ca31a58af953b31d074b470c5076b0f4b77fa76e527818974bae9",
            "Blake2b-384" to
                "35390115be9ccc14ade48b3956edd17d7199f704b633e41d34232514a74f98e31" +
                    "ba668af60aee73aa6138f3d580fce49",
            "Blake2b-512" to
                "3a692f22f67ff2e35caa8eb57428ba5d756215ef3586bc4726773882b9836c6b" +
                    "36d93fea1aaaf209ceec4fff90f92027712ed13921e647dc6c101ffe68a8eb97",
            "Blake2s-160" to "dc6df5a41883bd6e1578b0df0ca3e1b1ac07ac2c",
            "Blake2s-224" to "4d40b91efd09f54c20328d8898e65ee6b41cfdbf66f8ce3d233bd9c8",
            "Blake2s-256" to "8e34670981beba4b69dc9f1380f2d982faa1a8a35fbd4167b47f1cfe006defe8",
            "Blake3-256" to "8c9ac50b44000894f0b03216f7770850791676f54170a9b9505ebaf7a502e3eb",
            "DSTU7564-256" to "6c240f18372e703d4a6e987ef12ab14e2664d375bc63fe152492a62804d7aba4",
            "DSTU7564-384" to
                "dcf8952e13043da4df2705589641f4676d9d07aa21ec1a5b11525e53ed12838a" +
                    "e4b145f8cf5f935e3d7d1201f1956554",
            "DSTU7564-512" to
                "e83945c9b56620e48b50eaf381c801078e05b89d85043ed5790596584d7057801" +
                    "73c9d07ac2f0c559f05893d13ea9576d660ac0db5bb8885fac30a07a24ec51a",
            "Skein-256-128" to "dfb6780585fcb71b7f8725f3b8bc3db5",
            "Skein-256-160" to "69bc779ef32b63ed641594cac645e9cf3540aa8f",
            "Skein-256-224" to "71a135876f540e96dc9b429ac733a55f6459e7bfe53f565bca800937",
            "Skein-256-256" to "86177f9baa4bd5466585a4fd78b3ebd595d83b29b7c1be57807165c36e42ff26",
            "Skein-512-128" to "47ad96ccd88f8838bd5e776876507581",
            "Skein-512-160" to "3786cadb3d7bd07213851294862807d1a94e8df6",
            "Skein-512-224" to "687af7ed4d263145becb99e9b188ef900d167bb87a10474806538e8d",
            "Skein-512-256" to "815bff2b80d67853e2caf98f6d0238aa8362369e69ed86c9b5843e8ce9216c9a",
            "Skein-512-384" to
                "18bad4a9c0d25040f64e53fa4e01fbc74496bbead31377bb1478c1d2b31f2fb" +
                    "531cef35bf3ce7ec7d18a7f57ace0136f",
            "Skein-512-512" to
                "d90e389462fe8306d256875cccd28125fb28292ff14c7d173bb4cad39ead167" +
                    "ea31e040a53d503622924a98f731f9e3a37ae86649cb33c6f1501f4b91de5e03f",
            "Skein-1024-384" to
                "a33b38aa8cdc953107d5ba6b9957677aef43c11a7b1d72a7d62704c30a2cf" +
                    "c205e3c595f0c0d950e229e1b249a689d5d",
            "Skein-1024-512" to
                "9e87ef7a0a06369f3e2d78dee44f9f3cc98f6e34caad0ba81bae0aa952c8" +
                    "555bd50d9a6bcd60b5b482f27582634f08ae6b49aa841cc440ceb15a85a4c2158306",
            "Skein-1024-1024" to
                "1c0d819c9418963d02f9328f691466399e38d600e2933786db631d60a28cfa" +
                    "04be4c0e6c6f0eb5e0089d8e53a08f2b6fdd8d913d5bfc138517fd31b39e7f262f526051c520" +
                    "d65655ab4275ea510e809e1002435a857a1ee81e7893cfd23e479297341679e83b0b10a1cecbb" +
                    "06a1572098a1660a33e879d040b9243ff53ea6c24",
            "GOST3411" to "efaecc0e8f3165f5d727203a99966bc2436d993c8f3c5d0a46cc5eac40968af7",
            "GOST3411-2012-256" to
                "c143c9bf23e434130c6dd840afbebe9eb6c8436b859841ec9760bedd6914f75f",
            "GOST3411-2012-512" to
                "c13d5b690fe5e2e44d49c88d4e9a05aa39cafb10e4041d695b2e41524afac27" +
                    "0ab2ff2f61c183d4990017842e74d64c93387d0b84fdf80acb8d0282583e2fb12",
            "Haraka-256" to "f43b3529cab3bee02d70e6f70378ae7ffd301a3788297da3e144f2567cbaaac2",
            "Haraka-512" to "7ed63fd5e91c18422e550cca54765fe4cfb0eaf6dadd34d8cf336eae06be1acf",
            "CRC32" to "9ae0daaf",
            "TupleHash128" to "dc808b934916e3ebf334e0364ed6d26935c927b40358a64bd2016365a050b817",
            "TupleHash256" to
                "b213e1c4d0ac6359c6d311bbe0132120ae8ea12cbfbe7a593a7619805a8ccd57c542edaed16806c2" +
                    "6a1c16da745be3d7d009982512c8d26b21fff75fa6f127f1",
            "ParallelHash128" to "9dfeca9feb71166544dc0b34c1e730822c00ac8f893679f61a61a0de2ce88eae",
            "ParallelHash256" to
                "52e782840ba7b554b5d37a237acb8ea1006109b566d8b00f2a7df7153fe2991ec4102eb" +
                    "b8fa6dd00ed2c6499a59e06806bc0ec637a5250a794329cf32f335b0c",
            "CRC64" to "5c8b80482bac7809",
            "Adler32" to "074001a5",
            "md5(md5)" to "550e1bafe077ff0b0b67f4e32f29d751",
            "MD5(MD5)" to "ea405b607de5e4f6797640ab81f1767d",
            "md5(md5(md5))" to "579646aad11fae4dd295812fb4526245",
            "MD5(MD5(MD5))" to "d201afd3e79bf74cbdcac73e88b9b969",
            "md5(SHA1)" to "2bf1cf76a8b3b0e986943868dc79a868",
            "md5(SHA256)" to "a4de7c17e0f46ed5adee6ed4750d6eb3",
            "md5(SHA384)" to "ed264ab723f452972cc9acdf993712fb",
            "md5(SHA512)" to "32d938bec236b8d25ac5af4404f3f916",
            "LM" to "0182bd0bd4444bf836077a718ccdf409",
            "NTLM" to "259745cb123a52aa2e693aaacca2db52",
        )

    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
    }

    @Test
    @Ignore
    fun crc32() {
        CRC32()
            .apply { update("hello".toByteArray()) }
            .value
            .also { assertEquals("3610a686", it.toString(16)) }
        "hello".toByteArray().crc32().also { assertEquals("3610a686", it) }
    }

    @Test
    fun adler32() {
        "hello".toByteArray().adler32().also { assertEquals("062c0215", it) }
    }

    @Test
    @Ignore
    fun hash() {
        println("hello".hash("TupleHash128"))
        println("hello".hash("TupleHash256"))
        println("hello".hash("ParallelHash128"))
        println("hello".hash("ParallelHash256"))
        println("hello".hash("Blake3-256"))
        "hello".hash().also { assertEquals("5d41402abc4b2a76b9719d911017c592", it) }
        "hello".toByteArray().hash().also {
            assertEquals("5d41402abc4b2a76b9719d911017c592", it.toHex())
        }

        File(TEST_PRJ_DIR, "LICENSE").hash().also {
            assertEquals("219b0e44bbfc8ffd26c6cd91bb3c5138", it)
        }
    }

    @Test
    @Ignore
    fun allHash() {
        val digestController = DigestController()
        val testData = "12345678"

        for ((k, v) in ALGOS_HASH) {
            for (alg in v) {
                (if (k == "PasswordHashing") {
                        "PasswordHashing$alg"
                    } else {
                        "${k}${alg.takeIf { requireNotNull(ALGOS_HASH[k]).size > 1 }.orEmpty()}"
                            .replace("SHA2", "SHA-")
                            .replace(
                                "(Haraka|GOST3411-2012|Keccak|SHA3|Blake2b|Blake2s|DSTU7564|Skein)"
                                    .toRegex(),
                                "$1-"
                            )
                    })
                    .also {
                        println(it)
                        if (it.contains("PasswordHashing")) {
                            if (it.contains("SpringSecurity").not()) {
                                assertEquals(
                                    expectedMap[it.replace("PasswordHashing", "")],
                                    digestController.digest(
                                        it.replace("PasswordHashing", ""),
                                        testData
                                    )
                                )
                            }
                        } else if (it.contains("Windows")) {
                            if (it.contains("LM")) {
                                assertEquals(
                                    expectedMap[it.replace("Windows", "")],
                                    digestController.digest(it.replace("Windows", ""), testData)
                                )
                            }
                        } else if (it.contains("512")) {
                            assertEquals(expectedMap[it], testData.repeat(8).hash(it))
                        } else if (it.contains("256") && !it.contains("(SHA256)")) {
                            assertEquals(expectedMap[it], testData.repeat(4).hash(it))
                        } else {
                            assertEquals(expectedMap[it], digestController.digest(it, testData))
                        }
                    }
            }
        }
    }

    @Test
    @Ignore
    fun hashCount() {
        val data = "123456".toByteArray()
        val data2 = "123456"
        val count = 3
        // hash byte
        var tmp = data
        repeat(count) { tmp = tmp.hash() }
        println(tmp.toHex())
        //        assertEquals("4280d89a5a03f812751f504cc10ee8a5",tmp.toHex())

        // md5(md5($pass)  md5加密后，结果转换成小写,再进行md5
        assertEquals(
            "14e1b600b1fd579f47433b88e8d85291",
            PasswordHashingType.DoubleMd5.hash(data2.toByteArray())
        )

        // MD5(MD5($pass)) 解密  md5加密后，结果转换成大写,再进行md5
        assertEquals(
            "f59bd65f7edafb087a81d4dca06c4910",
            PasswordHashingType.DoubleMd5Uppercase.hash(data2.toByteArray())
        )

        // md5(md5(md5($pass))  md5加密后，结果转换成小写,再进行md5
        assertEquals(
            "c56d0e9a7ccec67b4ea131655038d604",
            PasswordHashingType.TripleMd5.hash(data2.toByteArray())
        )

        // MD5(MD5(MD5($pass))) 解密	 md5加密后，结果转换成大写,再进行md5
        assertEquals(
            "cf814721358d09942b255746542ad2a4",
            PasswordHashingType.TripleMd5Uppercase.hash(data2.toByteArray())
        )

        // md5(SHA1)
        assertEquals(
            "fe85e814fd656a2d490b842c6d33019d",
            PasswordHashingType.Md5Sha1.hash(data2.toByteArray())
        )

        // md5(SHA256)
        assertEquals(
            "05b371cbb333cb82d98b11d4f5960b9a",
            PasswordHashingType.Md5Sha256.hash(data2.toByteArray())
        )

        // md5(SHA384)
        assertEquals(
            "1de321163aa049944ad52f333b9c7c46",
            PasswordHashingType.Md5Sha384.hash(data2.toByteArray())
        )
        // md5(SHA512)
        assertEquals(
            "bb16e8d698bb2a61668c1eee494a777e",
            PasswordHashingType.Md5Sha512.hash(data2.toByteArray())
        )
    }

    @Test
    fun windowsHash() {
        val plain = "123456"
        plain.lmHash().also { assertEquals("44efce164ab921caaad3b435b51404ee", it) }
        plain.ntlmHash().also { assertEquals("32ed87bdb5fdc5e9cba88547376818d4", it) }
    }

    @Test
    fun mysqlHash() {
        val plain = "leon"
        assertEquals("*5F37A995E3A38A5807DEEA99D2CD1002BAC3DE5B", plain.mysql().uppercase())

        println(plain.mysqlOld())

        assertEquals("3d7bfc705698e189", plain.mysqlOld())
    }
}
