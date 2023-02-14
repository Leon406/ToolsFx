package me.leon.ctf

import java.io.File
import kotlin.test.*
import me.leon.TEST_CTF_DIR
import me.leon.classical.*
import me.leon.encode.base.BASE32_DICT
import me.leon.encode.base.BASE64_DICT
import me.leon.ext.*
import me.leon.ext.crypto.BINARY_REGEX
import me.leon.ext.crypto.HEX_REGEX

class CtfTest2 {
    @Test
    fun dnaDecode() {
        File(TEST_CTF_DIR, "dna.txt").readText().dnaDecode().also {
            assertTrue { it.startsWith("DNA is essentially") && it.endsWith("the blueprint.") }
        }
    }

    @Test
    fun caesarBox() {
        val height = 3
        val data = "Hello World!"
        val expected = "Hlodeor!lWl"

        assertEquals(expected, data.caesarBox(height))
        assertEquals("HelloWorld!", expected.caesarBoxDecrypt(height))
    }

    @Test
    fun rot8000() {
        val data = "The Quick Brown Fox Jumped Over The Lazy Dog."
        val expected = "籝籱籮 籚籾籲籬籴 籋类籸粀籷 籏籸粁 籓籾籶籹籮籭 籘籿籮类 籝籱籮 籕籪粃粂 籍籸籰簷"

        assertEquals(expected, data.rot8000())
        assertEquals(data, expected.rot8000())
    }

    @Test
    fun cetacean() {
        val data = "hi"
        val expected = "EEEEEEEEEeeEeEEEEEEEEEEEEeeEeEEe"

        assertEquals(expected, data.cetacean())
        assertEquals(data, expected.cetaceanDecrypt())
    }

    @Test
    fun yygqTest() {
        assertEquals("先套娃，alp只在最后一步用，注意数字哦", File(TEST_CTF_DIR, "yygq.txt").readText().yygqDecode())
    }

    @Test
    fun manchester() {
        //        val data = "你好manchester encode data"
        //        println(data.manchester())
        //        println(data.manchester().manchesterDecode2String())
        // 10110010
        // 标准 1001101001011001
        //  0110010110100110
        // 差分 1010011010100101
        // https://upload-images.jianshu.io/upload_images/7648905-6c4a8341a9f08b3e.png?imageMogr2/auto-orient/strip|imageView2/2/w/1007/format/webp

        val testData = "B2" // 10110010
        val testDataHex = "0xB2" // 10110010
        val testDataBinary = "0b10110010" // 10110010
        val encoded = "0110010110100110" // 10110010
        val encodedStandard = "1001101001011001" // 10110010

        assertEquals(encoded, testData.hex2ByteArray().manchester())
        assertEquals(encoded, testDataHex.manchester())
        assertEquals(encoded, testDataBinary.manchester())

        assertEquals("10110010", testData.hex2ByteArray().manchester().manchesterDecode())
        assertEquals(encodedStandard, testDataHex.manchester(true))
        assertEquals("10110010", encodedStandard.manchesterDecode(true))
        assertEquals("1010011010100101", testDataHex.manchesterDiff())

        assertEquals("10110010", testDataHex.manchesterDiff().manchesterDiffDecode())

        "5555555595555A65556AA696AA6666666955".manchesterDecode(isReverse = true).also {
            //            println(it)
            assertEquals("fffffed31f645055f9", it.binary2ByteArray().toHex())
        }
    }

    // https://shawroot.hatenablog.com/entry/2020/02/03/BJDCTF2020/BUUCTF-CRYPTO:%E7%BC%96%E7%A0%81%E4%B8%8E%E8%B0%83%E5%88%B6
    @Test
    fun manchester2() {
        val encode =
            "0x2559659965656A9A65656996696965A6695669A9695A699569666A5A6A6569666A59695A69AA696569666AA6" // 10110010
        val plain = "424a447b4469664d616e63686573746572636f64657d"
        assertEquals(plain, encode.manchesterDecode(true).binary2ByteArray().toHex())
    }

    @Test
    fun autoDecode() {
        val format = "%9s\t%6s\t%6s"
        val testData =
            listOf(
                "0b1110101",
                "0B1110101",
                "1110101",
                "1310103",
                "1310109",
                "0O1310103",
                "0o1310103",
                "abCD",
                "0xabCD",
                "0XabCD",
            )

        println(format.format("raw", "binary", "hex"))
        testData.forEach {
            println(it.autoDecodeToByteArray().toHex())
            println(format.format(it, BINARY_REGEX.matches(it), HEX_REGEX.matches(it)))
        }
    }

    @Test
    fun eight() {
        val raw = "abcefghijklmoqrsttuvwxyzhelloo12"
        val encode = "升困艮益蛊困蛊无妄井萃噬嗑既济井兑损离巽履晋节恒履蒙归妹鼎讼蛊履大过否噬嗑需井萃未济丰巽萃大有同人小过涣谦"
        assertEquals(encode, raw.eightDiagram())
        assertEquals(raw, encode.eightDiagramDecode())
    }

    @Test
    fun sexagesimal() {
        val plain = "你好"
        val encode = "乙丑癸巳甲寅己亥丁卯甲申丁未甲午己巳"
        assertEquals(encode, plain.stemBranch())
        assertEquals(plain, encode.stemBranchDecode())
    }

    @Test
    fun fenhamTest() {
        val key = "crude"
        val plain = "hello"
        val encoded = "00010110010111001100100010000001010"
        assertEquals(encoded, plain.fenham(key))
        assertEquals(plain, encoded.fenhamDecrypt(key))
    }

    @Test
    fun fracMorseTest() {
        val key = "ROUNDTABLECFGHIJKMPQSVWXYZ"
        val plain = "hello"
        val encoded = "RAQUNBI"

        assertEquals(encoded, plain.fracMorse(key))
        assertEquals(plain, encoded.fracMorseDecrypt(key))
    }

    @Test
    fun twinHexTest() {
        // a 4tc  a1 4tt  a12 4tt1c0

        assertEquals("4tc", "a".twinHex())
        assertEquals("4tt", "a1".twinHex())
        assertEquals("4tt1c0", "a12".twinHex())

        assertEquals("a", "4tc".twinHexDecrypt())
        assertEquals("a1", "4tt".twinHexDecrypt())
        assertEquals("a12", "4tt1c0".twinHexDecrypt())
    }

    @Test
    fun baiJiaXing() {
        val data = "baijiaxing"
        val encode = "卫褚尤许尤褚谢尤张朱"
        assertEquals(encode, data.baiJiaXing())
        assertEquals(data, encode.baiJiaXingDecode())
    }

    @Test
    fun base64Steg() {

        File(TEST_CTF_DIR, "base64steg.txt").readText().baseStegDecrypt().also {
            assertEquals("Base_sixty_four_point_five", it)
        }

        File(TEST_CTF_DIR, "base64steg2.txt").readText().baseStegDecrypt().also {
            assertEquals("GXY{fazhazhenhaoting}", it)
        }
    }

    @Test
    fun base32Steg() {

        File(TEST_CTF_DIR, "base32steg.txt").readText().baseStegDecrypt(BASE32_DICT).also {
            assertEquals("flag{afd0e09383751ac6e81bbf71925dfaf8}", it)
        }
    }

    @Test
    fun base64StegEncode() {
        (1..20).forEach {
            val r = BASE64_DICT.random(it)
            val encrypt = r.baseStegEncrypt(File(TEST_CTF_DIR, "raw.txt").readText())
            assertEquals(r, encrypt.baseStegDecrypt())
        }
    }

    @Test
    fun base32StegEncode() {
        (1..20).forEach {
            val r = BASE64_DICT.random(it)
            val encrypt = r.baseStegEncrypt(File(TEST_CTF_DIR, "raw.txt").readText(), BASE32_DICT)
            assertEquals(r, encrypt.baseStegDecrypt(BASE32_DICT))
        }
    }

    @Test
    fun type7() {
        val data = "flag{type7}"
        val encoded = data.type7(2)
        assertEquals("0200085a0c1d1b385c4b5e04", encoded)
        assertEquals(data, encoded.type7Decode())
    }

    @Test
    fun citrixCtx1() {
        // JEDB
        val data = "1"
        val encoded = "JEDB"
        assertEquals(encoded, data.citrixCtx1())
        assertEquals(data, encoded.citrixCtx1Decode())
    }
}
