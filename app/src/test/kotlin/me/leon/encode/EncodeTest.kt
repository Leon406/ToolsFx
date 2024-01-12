package me.leon.encode

import java.io.File
import kotlin.system.measureNanoTime
import kotlin.test.Ignore
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import me.leon.TEST_ENCODE_DIR
import me.leon.classical.hackWord
import me.leon.classical.hackWordDecode
import me.leon.controller.EncodeController
import me.leon.ctf.*
import me.leon.encode.base.*
import me.leon.ext.*
import me.leon.ext.crypto.EncodeType
import me.leon.ext.crypto.encodeTypeMap
import org.junit.Before
import org.junit.Test

@Ignore
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
        assertEquals(base64, controller.encode2String(raw, EncodeType.BASE64))
        assertEquals(raw, controller.decode2String(base64, EncodeType.BASE64))

        val binary =
            "1110010110111100100000001110010110001111100100011110010110110111101001011110010110000" +
                "101101101111110100110011011100001101110010110010000100010000010000001100010011110010010000" +
                "001101100011001010110111101101110001101000011000000110110010000000011010100110010011100000" +
                "1101111011010100110100101100101001011100110001101101110"
        assertEquals(binary, controller.encode2String(raw, EncodeType.BINARY))
        assertEquals(raw, controller.decode2String(binary, EncodeType.BINARY))

        val hex = "e5bc80e58f91e5b7a5e585b7e99b86e59088206279206c656f6e343036403532706f6a69652e636e"
        assertEquals(hex, controller.encode2String(raw, EncodeType.HEX))
        assertEquals(raw, controller.decode2String(hex, EncodeType.HEX))

        val unicode =
            "\\u5f00\\u53d1\\u5de5\\u5177\\u96c6\\u5408\\u0020\\u0062\\u0079\\u0020\\u006c\\u0065\\u006f\\u006e" +
                "\\u0034\\u0030\\u0036\\u0040\\u0035\\u0032\\u0070\\u006f\\u006a\\u0069\\u0065\\u002e\\u0063\\u006e"
        assertEquals(unicode, controller.encode2String(raw, EncodeType.UNICODE))
        assertEquals(raw, controller.decode2String(unicode, EncodeType.UNICODE))

        val urlEncode =
            "%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7%E9%9B%86%E5%90%88%20by%20leon406%4052pojie.cn"
        assertEquals(urlEncode, controller.encode2String(raw, EncodeType.URL_ENCODE))
        assertEquals(raw, controller.decode2String(urlEncode, EncodeType.URL_ENCODE))

        val urlBase64 = "5byA5Y-R5bel5YW36ZuG5ZCIIGJ5IGxlb240MDZANTJwb2ppZS5jbg"
        assertEquals(urlBase64, controller.encode2String(raw, EncodeType.BASE64_URL))
        assertEquals(raw, controller.decode2String(urlBase64, EncodeType.BASE64_URL))

        val base58 = "CR58UvatBfMNr917q5LwvMbAtrpuA5s3iCQe5eDivFqEz8LN1Ytu6aH"
        assertEquals(base58, controller.encode2String(raw, EncodeType.BASE58))
        assertEquals(raw, controller.decode2String(base58, EncodeType.BASE58))

        val base58Check = "2HhMuaDzQFGwDdVBD7S8MJRYAspzUi9zUGCLeQ1hsAdBGXBnq7FnKXsTc2iFp"
        assertEquals(base58Check, controller.encode2String(raw, EncodeType.BASE58_CHECK))
        assertEquals(raw, controller.decode2String(base58Check, EncodeType.BASE58_CHECK))

        // test url https://www.better-converter.com/Encoders-Decoders/Base62-Encode
        val base62Map = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val base62 = "JJLamodrHXspZr5qUcfZYO3u0Gdw3fhzQqxO834pCgRbqcvOn3Vkju"
        assertEquals(base62, raw.radixNEncode(base62Map))
        assertEquals(base62, raw.base62())
        assertEquals(raw, base62.radixNDecode2String(base62Map))
        assertEquals(raw, base62.base62Decode2String())

        val base36Map = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val base36 = "MAHJV1X5YMIHRRDJ0HQLTZ0WNFLYDP0W01ME2E8MTAT3QNDXRXGNH7HJYAYY5Q"
        assertEquals(base36, raw.radixNEncode(base36Map))
        assertEquals(base36, raw.base36())
        assertEquals(raw, base36.radixNDecode2String(base36Map))
        assertEquals(raw, base36.base36Decode2String())
    }

    @Test
    fun baseNTest() {
        val base36Map = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val encoded = "MAHJV1X5YMIHRRDJ0HQLTZ0WNFLYDP0W01ME2E8MTAT3QNDXRXGNH7HJYAYY5Q"

        assertEquals(encoded, raw.radixNEncode(base36Map))
        assertEquals(raw, encoded.radixNDecode2String(base36Map))
    }

    @Test
    fun b45() {
        assertEquals("BB8", "AB".base45())
        assertEquals("%69 VD92EX0", "Hello!!".base45())
        assertEquals("C-SEFK*.KRT9-3E+Q691", "你好Leon406".base45())

        assertEquals("你好Leon406", "C-SEFK*.KRT9-3E+Q691".base45Decode2String())
        assertEquals("ietf!", "QED8WEX0".base45Decode2String())
        assertEquals("Hello!!", "%69 VD92EX0".base45Decode2String())
    }

    @Test
    fun b92() {
        val encoded = "a[:hQLeff={07_Q]1SQUCG}LfVG!U^;m1t*EplJB2TX6},?iTB"
        val encoded91 = "@iH<lc*;oUp/im\"QPl`yR*ie}NK;.D!Xu)b:J[Rj+6KKM7P"
        val encoded92 = "sjT_Vni^B1<]D9f:XapY99'b/v8l*vMG4B\$E!<Ws\$JmoAFJMHa"

        assertEquals("flag(wMt84iS06mCbbfuOfuVXCZ8MSsAFN1GA}", encoded91.base91Decode2String())
        assertEquals(encoded, raw.base91())
        assertEquals(encoded92, raw.base92Encode2String())

        assertEquals(raw, encoded.base91Decode2String())
        assertEquals(raw, encoded92.base92Decode2String())
    }

    @Test
    fun asciiPrint() {
        for (i in 33..127) print(i.toChar().toString())
        println(encodeTypeMap)
    }

    @Test
    fun escape() {
        val d = "%u5f00%u53d1%u5de5%u5177%u96c6%u5408%20by%20leon406%4052pojie%2ecn"
        assertEquals(d, raw.escape())
        assertEquals(raw, d.unescape2String())

        println(
            raw.octal().also {
                println(it.octalDecode2String())
                assertEquals(raw, it.octalDecode2String())
            }
        )
        println(
            raw.decimal().also {
                println(it.decimalDecode2String())
                assertEquals(raw, it.decimalDecode2String())
            }
        )

        val msg =
            "/119/101/108/99/111/109/101/116/111/97/116/116/97/99/107/97/110/100/100/101/102/101" +
                "/110/99/101/119/111/114/108/100"

        assertEquals("welcometoattackanddefenceworld", msg.decimalDecode2String())
    }

    @Test
    fun xxUUEncodeTest() {
        val xxEncoded = "ctPm+tMyFtPSZtMKruNi4tN06647t64lZPqsoA1N+BH7kPqddNGtXPU++"
        val uuEncoded = "HY;R`Y8^1Y;>EY86WZ9N&Y9\"((&)Y(&QE;VXT,#9`-3)P;VII92YC;@``"

        assertEquals(xxEncoded, raw.xxEncode())
        assertEquals(uuEncoded, raw.uuEncode())

        assertEquals(raw, xxEncoded.xxDecode2String())
        assertEquals(raw, uuEncoded.uuDecode2String())
    }

    @Test
    fun bubbleBabble() {
        assertEquals("xexax", "".bubbleBabble())
        assertEquals("xesef-disof-gytuf-katof-movif-baxux", "1234567890".bubbleBabble())
        assertEquals(
            "1234567890",
            "xesef-disof-gytuf-katof-movif-baxux".bubbleBabbleDecode2String()
        )
        assertEquals(
            "Pineapple1",
            "xigak-nyryk-humil-bosek-sonaf-cuxix".bubbleBabbleDecode2String()
        )
        assertEquals(
            "Pineap ple3",
            "xigak-nyryk-humil-bimel-byrik-hesox".bubbleBabbleDecode2String()
        )
        assertEquals(
            "Pineapplea",
            "xigak-nyryk-humil-bosek-sonak-cuxux".bubbleBabbleDecode2String()
        )
    }

    @Test
    fun decodeUnicode() {
        val u = "&#20320;&#22909;&#20013;&#22269;&#x4e2d;&#x56fd;&#X56FD;"
        println(u.unicode2String())
        assertEquals("🗻", "🗻".toUnicodeString().unicode2String())
        assertEquals("🗻", "🗻".toUnicodeString().unicode2String())

        assertContentEquals(
            arrayOf("🗾", "🗾"),
            arrayOf("&#128510;".unicode2String(), "128510".toInt().toUnicodeChar())
        )

        assertContentEquals(
            intArrayOf(128_510, 128_507),
            intArrayOf("\uD83D\uDDFE".unicodeCharToInt(), "🗻".unicodeCharToInt())
        )
        println("🗾".unicodeCharToInt())
    }

    @Test
    fun hex2Base64() {
        "e4bda0e5a5bd4c656f6e21".hex2ByteArray().base64().also {
            assertEquals("5L2g5aW9TGVvbiE=", it)
        }
    }

    @Test
    fun baseNEncode() {

        val base58 = "CR58UvatBfMNr917q5LwvMbAtrpuA5s3iCQe5eDivFqEz8LN1Ytu6aH"
        assertEquals(base58, raw.base58())
        assertEquals(raw, base58.base58Decode2String())
        assertEquals(
            "flag{8ea44e39c914c5ddfbb9808c10033421}",
            "G9mzcaHeFrtWbmbyVxTUN1NeWS1kNJiYRU41cZcaYq9Hsor7QnA8".base58Decode2String()
        )

        measureNanoTime {
                raw.toByteArray().baseCheck().also {
                    assertEquals(raw, String(it.baseCheckDecode()))
                }
            }
            .also { println("total $it") }

        measureNanoTime {
                raw.base58Check().also { assertEquals(raw, it.base58CheckDecode2String()) }
            }
            .also { println("total2 $it") }
    }

    @Test
    fun unicodeMix() {
        val raw =
            """
            "title": "\u7b2c\u4e00\u6a21\u5757 - \u7ecf\u6d4e\u5b66\u6838\u5fc3\u539f\u7406",
            "file_name": "\u+6a21\U5757\u5b8c\u6574\u7248",
       """
                .trimIndent()

        val decoded =
            """
            "title": "第一模块 - 经济学核心原理",
            "file_name": "模块完整版",
        """
                .trimIndent()
        assertEquals(decoded, raw.unicodeMix2String())
    }

    @Test
    fun htmlEntity() {
        val s = "<a>ddfd</a>"
        s.toHtmlEntity().also { assertEquals(s, it.htmlEntity2String()) }
        s.toHtmlEntity(isAll = false).also { assertEquals(s, it.htmlEntity2String()) }
        "&#39;Steve&#39;".htmlEntity2String().also { assertEquals("'Steve'", it) }
    }

    @Test
    fun quotePrint() {
        val raw = "开发工具集合 by leon406@52pojie.cn"
        val encrypted = "=bf=aa=b7=a2=b9=a4=be=df=bc=af=ba=cf=20by=20leon406@52pojie.cn"
        val gbkMsg = QuotePrintable.encode(raw, "gbk")

        assertEquals(encrypted, gbkMsg)
        assertEquals(raw, QuotePrintable.decode(gbkMsg, "gbk"))
        assertEquals("好", QuotePrintable.decode("=e5=a5=bd"))
    }

    @Test
    fun unicode() {
        val da =
            "\\xf0\\x9f\\x99\\x83\\xf0\\x9f\\x92\\xb5\\xf0\\x9f\\x8c\\xbf\\xf0\\x9f\\x8e\\xa4\\xf0\\x9f" +
                "\\x9a\\xaa\\xf0\\x9f\\x8c\\x8f\\xf0\\x9f\\x90\\x8e\\xf0\\x9f\\xa5\\x8b\\xf0\\x9f\\x9a\\xab"

        assertEquals("🙃💵🌿🎤🚪🌏🐎🥋🚫", da.jsHexDecodeString())
    }

    @Test
    fun radix64() {
        assertEquals("KRGx", "123".radix64())
        assertEquals("KRGxL.", "1234".radix64())
        assertEquals("KRGxLBS", "12345".radix64())
        assertEquals("KRGxLBS0", "123456".radix64())
    }

    @Test
    fun radix() {
        val raw =
            "19WIG196SRWK1R6OGHT6EPDXM48A1RT5SSWKMVNZN1W46WGFF15F3NV9WN5CDK7WZTR0HXXJLWZ89KSPOQS1BYQRY53FIBPAL" +
                "3NH4H9VQDYBOUWDZ1BTLGETOD1CJNESMW48BPM1WNFZZSZGEVYNNSCDLR6X754LJIGIPCKHJV8RMZOH6OQ4X5XELVH1L" +
                "73G4B5GQ83O4N8802OPP83510DUT2H4YJORJMVIVIZ4STKI1BAZ4R5VP1MM3Z2HHNLZ108JUA5IFPJL21U8TVL5IH6" +
                "LQTHDFH9YOIZJVKZY0IRVXDMOFI7LAXB2P50RAP6H33UHGMDR4TV0TN3H2YBXM11Z8FONNAOEGL31AN42OTX7LZX61" +
                "F98G32KJFGPP6WD1ZFWMUKBH7FMT"

        EncodeType.BASE36.decode(raw, "", "UTF-8").also {
            println(it)
            EncodeType.RADIX10.encode2String(it, "", "UTF-8").also { println(it) }
        }
    }

    @Test
    fun b85() {
        "".toByteArray().base85().also { assertEquals("", it) }
        "1".toByteArray().base85().also {
            assertEquals("0`", it)
            assertEquals("1", it.base85Decode().decodeToString())
        }
        "12".toByteArray().base85().also {
            assertEquals("0er", it)
            assertEquals("12", it.base85Decode().decodeToString())
        }
        "123".toByteArray().base85().also {
            assertEquals("0etN", it)
            assertEquals("123", it.base85Decode().decodeToString())
        }
        "1234".toByteArray().base85().also {
            assertEquals("0etOA", it)
            assertEquals("1234", it.base85Decode().decodeToString())
        }
        "12345".toByteArray().base85().also {
            assertEquals("0etOA2#", it)
            assertEquals("12345", it.base85Decode().decodeToString())
        }

        "123".toByteArray().base85(Z85_DICT).also {
            assertEquals("f!\$J", it)
            assertEquals("123", it.base85Decode(Z85_DICT).decodeToString())
        }
        "123".toByteArray().base85(BASE85_IPV6_DICT).also {
            assertEquals("F)}j", it)
            assertEquals("123", it.base85Decode(BASE85_IPV6_DICT).decodeToString())
        }
    }

    @Test
    fun b85Decode() {
        "9jn"
            .map { BASE85_DICT.indexOf(it) }
            .chunked(5)
            .map {
                val count =
                    when (it.size) {
                        2 -> 1
                        3 -> 2
                        4 -> 3
                        else -> 4
                    }
                (it.getOrElse(0) { 0 } * 85 * 85 * 85 * 85 +
                        it.getOrElse(1) { 255 } * 85 * 85 * 85 +
                        it.getOrElse(2) { 255 } * 85 * 85 +
                        it.getOrElse(3) { 255 } * 85 +
                        it.getOrElse(4) { 255 })
                    .toBigInteger()
                    .toByteArray()
                    .take(count)
            }
            .flatten()
            .toByteArray()
            .also { println(it.decodeToString()) }
    }

    @Test
    fun utf7() {
        println("&VMhUyFTIdoSQ/WYv-".uft7ExtDecode())
        println("Hello, World!".utf7())
        println("1 + 1 = 2".utf7())
        println("x';xss:expression(alert(1));font-family:'".utf7())
        println("x';xss:expression(alert(1));font-family:'".utf7(true))
        println("£1".utf7())

        val all = "+VMhUyFTIdoSQ/WYv-"
        println("哈哈哈的都是".utf7())
        println("哈哈哈&12的都是".utf7Ext())
        //
        println(
            ("+AHgAJwA7AHgAcwBzADoAZQB4AHAAcgBlAHMAcwBpAG8AbgAoAGEAbABlAHIAdAAoADEAKQApADsAZgBvA" +
                    "G4AdAAtAGYAYQBtAGkAbAB5ADoAJw-")
                .uft7Decode()
        )
        println("&VMhUyFTI-&-12&doSQ/WYv-".uft7ExtDecode())
        println(all.uft7Decode())

        println("A编码示例bC+12".utf7())
    }

    @Test
    fun crack() {
        val inputText =
            "4B595954494D32515046324757595A534E52415653334357474E4A575955544E4B5A4D46434F4B5947425346" +
                "4D5A444E4D51334557524B5A4F424944473542554B595A44534B324E49565746515532464B4934" +
                "5649564B464E4E494543504A35"
        val propInput = inputText.split(".+ :\\s*".toRegex()).filterNot(String::isBlank).first()
        println("$inputText \n $propInput")
        println(controller.decode2String(propInput, EncodeType.BASE16, ""))
        EncodeType.values()
            .map { it.type to controller.decode2String(propInput, it, "") }
            .filterNot {
                it.second.isEmpty() ||
                    it.second.contains(propInput, true) ||
                    it.second.contains("[\u0000-\u001F]|解码错误:|�".toRegex())
            }
            .joinToString("\n") {
                println("__" + it.second + "__")
                it.first + " :\t" + it.second
            }
            .also { println(it) }
    }

    @Test
    fun mix() {
        val data = File(TEST_ENCODE_DIR, "mix_bin_oct_hex.txt").readText()
        println(data.mixDecode2String())

        val msg = "你好leon406"
        val message = msg.mixEncode()
        assertEquals(msg, message.mixDecode2String())
        println("0b110001".mixDecode2String())
        println("0o61".mixDecode2String())
        println("0x31".mixDecode2String())
    }

    @Test
    fun radixNTest() {
        val base32 = "4W6IBZMPSHS3PJPFQW36TG4G4WIIQIDCPEQGYZLPNY2DANSAGUZHA33KNFSS4Y3O"
        assertEquals(base32, controller.encode2String(raw, EncodeType.BASE32))
        assertEquals(base32, raw.radixNEncode(BASE32_DICT))
        assertEquals(raw, controller.decode2String(base32, EncodeType.BASE32))
        assertEquals(raw, base32.radixNDecode2String(BASE32_DICT))

        val base16 =
            "E5BC80E58F91E5B7A5E585B7E99B86E59088206279206C656F6E343036403532706F6A69652E636E"
        assertEquals(base16, controller.encode2String(raw, EncodeType.BASE16))
        assertEquals(base16, raw.radixNEncode(BASE16_DICT))
        assertEquals(raw, controller.decode2String(base16, EncodeType.BASE16))
        assertEquals(raw, base16.radixNDecode2String(BASE16_DICT))
        val base37 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_"

        println(controller.encode2String("37123123", EncodeType.DECIMAL_RADIX_N, base37))
        assertEquals("JTWZO", "37123123".toBigInteger().radixNEncode(base37))
        assertEquals("37123123", "JTWZO".radixNDecode2DecimalString(base37.map { it.toString() }))
    }

    @Test
    fun caseCrack() {
        val encode = "ZMXHZ3TZMHVFSDR2M19GMHVUZF83ADNFUJFNADDFNG41DZNYFQ=="
        val raw = "flag{Y0u_H4v3_F0und_7h3_R1gh7_4n5w3r}"
        println(encode.base64CaseCrack())
        assertEquals(encode, raw.base64UpperCase())
    }

    @Test
    fun hackString() {
        val hackWord =
            ("This message serves to prove how our minds can do amazing things!" +
                    "In the beginning it was hard but now, on this line your mind is reading it automatically" +
                    " without even thinking about it, be proud!Only certain people can read this!")
                .hackWord()
        println(hackWord)
        println(hackWord.hackWordDecode().lowercase())
    }
}
