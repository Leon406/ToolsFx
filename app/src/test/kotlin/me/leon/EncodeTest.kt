package me.leon

import kotlin.test.assertEquals
import me.leon.controller.EncodeController
import me.leon.ctf.bubbleBabble
import me.leon.ctf.bubbleBabbleDecode2String
import me.leon.encode.*
import me.leon.encode.base.*
import me.leon.ext.*
import org.junit.Before
import org.junit.Test

class EncodeTest {

    lateinit var controller: EncodeController
    private val raw = "开发工具集合 by leon406@52pojie.cn"

    @Before
    fun setUp() {
        controller = EncodeController()
    }

    @Test
    fun encode() {
        val base64 = "5byA5Y+R5bel5YW36ZuG5ZCIIGJ5IGxlb240MDZANTJwb2ppZS5jbg=="
        assertEquals(base64, controller.encode2String(raw, EncodeType.Base64))
        assertEquals(raw, controller.decode2String(base64, EncodeType.Base64))

        val base32 = "4W6IBZMPSHS3PJPFQW36TG4G4WIIQIDCPEQGYZLPNY2DANSAGUZHA33KNFSS4Y3O"
        assertEquals(base32, controller.encode2String(raw, EncodeType.Base32))
        assertEquals(base32, raw.baseNEncode(32, BASE32_DICT))
        assertEquals(raw, controller.decode2String(base32, EncodeType.Base32))
        assertEquals(raw, base32.baseNDecode2String(32, BASE32_DICT))

        val base16 =
            "E5BC80E58F91E5B7A5E585B7E99B86E59088206279206C656F6E343036403532706F6A69652E636E"
        assertEquals(base16, controller.encode2String(raw, EncodeType.Base16))
        assertEquals(base16, raw.baseNEncode(16, BASE16_DICT))
        assertEquals(raw, controller.decode2String(base16, EncodeType.Base16))
        assertEquals(raw, base16.baseNDecode2String(16, BASE16_DICT))

        val binary =
            "1110010110111100100000001110010110001111100100011110010110110111101001011110010110000" +
                "101101101111110100110011011100001101110010110010000100010000010000001100010011110010010000" +
                "001101100011001010110111101101110001101000011000000110110010000000011010100110010011100000" +
                "1101111011010100110100101100101001011100110001101101110"
        assertEquals(binary, controller.encode2String(raw, EncodeType.Binary))
        assertEquals(raw, controller.decode2String(binary, EncodeType.Binary))

        val hex = "e5bc80e58f91e5b7a5e585b7e99b86e59088206279206c656f6e343036403532706f6a69652e636e"
        assertEquals(hex, controller.encode2String(raw, EncodeType.Hex))
        assertEquals(raw, controller.decode2String(hex, EncodeType.Hex))

        val unicode =
            "\\u5f00\\u53d1\\u5de5\\u5177\\u96c6\\u5408\\u20\\u62\\u79\\u20\\u6c\\u65\\u6f\\u6e\\u34" +
                "\\u30\\u36\\u40\\u35\\u32\\u70\\u6f\\u6a\\u69\\u65\\u2e\\u63\\u6e"
        assertEquals(unicode, controller.encode2String(raw, EncodeType.Unicode))
        assertEquals(raw, controller.decode2String(unicode, EncodeType.Unicode))

        val urlEncode =
            "%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7%E9%9B%86%E5%90%88%20by%20leon406%4052pojie.cn"
        assertEquals(urlEncode, controller.encode2String(raw, EncodeType.UrlEncode))
        assertEquals(raw, controller.decode2String(urlEncode, EncodeType.UrlEncode))

        val urlBase64 = "5byA5Y-R5bel5YW36ZuG5ZCIIGJ5IGxlb240MDZANTJwb2ppZS5jbg=="
        assertEquals(urlBase64, controller.encode2String(raw, EncodeType.Base64Safe))
        assertEquals(raw, controller.decode2String(urlBase64, EncodeType.Base64Safe))

        val base58 = "CR58UvatBfMNr917q5LwvMbAtrpuA5s3iCQe5eDivFqEz8LN1Ytu6aH"
        assertEquals(base58, controller.encode2String(raw, EncodeType.Base58))
        assertEquals(raw, controller.decode2String(base58, EncodeType.Base58))

        val base58Check = "2HhMuaDzQFGwDdVBD7S8MJRYAspzUi9zUGCLeQ1hsAdBGXBnq7FnKXsTc2iFp"
        assertEquals(base58Check, controller.encode2String(raw, EncodeType.Base58Check))
        assertEquals(raw, controller.decode2String(base58Check, EncodeType.Base58Check))

        // test url https://www.better-converter.com/Encoders-Decoders/Base62-Encode
        val base62Map = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val base62 = "JJLamodrHXspZr5qUcfZYO3u0Gdw3fhzQqxO834pCgRbqcvOn3Vkju"
        assertEquals(base62, raw.baseNEncode(62, base62Map))
        assertEquals(base62, raw.base62())
        assertEquals(raw, base62.baseNDecode2String(62, base62Map))
        assertEquals(raw, base62.base62Decode2String())

        val base36Map = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val base36 = "MAHJV1X5YMIHRRDJ0HQLTZ0WNFLYDP0W01ME2E8MTAT3QNDXRXGNH7HJYAYY5Q"
        assertEquals(base36, raw.baseNEncode(36, base36Map))
        assertEquals(base36, raw.base36())
        assertEquals(raw, base36.baseNDecode2String(36, base36Map))
        assertEquals(raw, base36.base36Decode2String())
    }

    @Test
    fun baseNTest() {
        val base36Map = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        println("${0.toChar()}leon".baseNEncode(36, base36Map))
        println("0U2QMPA".baseNDecode2String(36, base36Map))

        Base91.encode(raw.toByteArray()).also { println(String(it)) }
        println("example string".base91())
        println(
            String(
                Base91.decode(
                    "5)GfG?ue\$y+/ZQ;mMB".also { println(it.base91Decode2String()) }.toByteArray()
                )
            )
        )
    }

    @Test
    fun b92() {
        //        println('#'.base92Int())
        //        println('!'.base92Int())
        //        println('_'.base92Int())
        //        println('0'.base92Int())
        //        println('}'.base92Int())
        //        println('D'.base92Int())
        //        println('8'.base92Int())
        //        println('*'.base92Int())
        //
        //        println(0.base92Char())
        //        println(1.base92Char())
        //        println(34.base92Char())
        //        println(10.base92Char())
        //        println(61.base92Char())
        //        println(62.base92Char())
        //        println(90.base92Char())

        println(String(Base91.encode(raw.toByteArray())))
        println("a".base92Encode2String())
        //        println("a".())
        println("D,".base92Decode2String())
        println("sjT_Vni^B1<]D9f:XapY99'b/v8l*vMG4B\$E!<Ws\$JmoAFJMHa".base92Decode2String())
        println("a[:hQLeff={07_Q]1SQUCG}LfVG!U^;m1t*EplJB2TX6},?iTB".base91Decode2String())
        //        println(String("".base92Encode()))
        //        println("D81RPya.)hgNA(%s".base92Decode())
        //        println("~".base92Decode())
        //        println(String("aaaaaaaaaaaaa".base92Encode()))
    }

    @Test
    fun asciiPrint() {
        for (i in 33..127) print(i.toChar().toString())
        println(encodeTypeMap)
    }

    @Test
    fun baseT() {
        println("ab".base92Encode2String())
        println("ab".baseNEncode(91, BASE92_DICT))
    }

    @Test
    fun escape() {
        val d = "%u5F00%u53D1%u5DE5%u5177%u96C6%u5408%20by%20leon406@52pojie.cn"
        println(EscapeUtils.escape(5.toChar() + raw))
        println(raw.escape())
        println(EscapeUtils.unescape(d))
        println(d.unescape2String())
        println(raw.octal().also { println(it.octalDecode2String()) })
        println(raw.decimal().also { println(it.decimalDecode2String()) })
    }

    @Test
    fun xxEncodeTest() {

        for (i in 0..5) {
            raw.repeat(i).uuEncode().also {
                println(it)
                println(it.uuDecode2String())
            }

            raw.repeat(i).xxEncode().also {
                println(it)
                println(it.xxDecode2String())
            }
        }
    }

    @Test
    fun bubbleBabble() {
        println("".bubbleBabble())
        println("1234567890".bubbleBabble().bubbleBabbleDecode2String())
        println("Pineap ple3ddfdsf dsf".bubbleBabble().bubbleBabbleDecode2String())
        println("xigak-nyryk-humil-bosek-sonaf-cuxix".bubbleBabbleDecode2String())
        println("xigak-nyryk-humil-bimel-byrik-hesox".bubbleBabbleDecode2String())
        "xigak-nyryk-humil-bosek-sonak-cuxux".bubbleBabbleDecode2String()
    }

    @Test
    fun hexx() {
        // -107 10010101
        //        "95".hex2ByteArray().also { println(it.contentToString()) }.toBinaryString().also
        // {
        //            println(it)
        //        }

        println(Long.MAX_VALUE)
        println(0x7FFFFFFFFFFFFFFFL)
        println("123".crc64())
    }

    @Test
    fun crc() {
        println((-0x3693a86a2878f0be).toULong().toLong().toString(16))
        println((0xD80000000000000).toULong().toLong())
        val toLong = 0xD80000000000000
        println(toLong.toULong())
        val path = "D:\\360极速浏览器下载\\0013_FLASHDRIVER_Addr_04.txt"
        val readBytes = path.toFile().readBytes()
        readBytes.run {
            CRC64()
                .apply {
                    update(this@run)
                    println(this.crcHex())
                }
                .crcDecimal()
                .also { println(it) }
        }
    }

    @Test
    fun charsets() {
        val raw = "你好"
        val charset = Charsets.UTF_16BE
        val charset2 = Charsets.UTF_16LE
        charset.encode(raw).also { println(charset.decode(it)) }
        raw.toHex().also { println(it) }
        raw.toByteArray(Charsets.UTF_16).toHex().also { println(it) }
        raw.toByteArray(charset).toHex().also { println(it) }
        raw.toByteArray(charset2).toHex().also { println(it) }
    }

    @Test
    fun unicodeMix() {
        val raw =
            """
            "title": "\u7b2c\u4e00\u6a21\u5757 - \u7ecf\u6d4e\u5b66\u6838\u5fc3\u539f\u7406",
            "file_name": "\u+6a21\U5757\u5b8c\u6574\u7248",
       """.trimIndent()
        println(raw.unicodeMix2String())
    }
}
