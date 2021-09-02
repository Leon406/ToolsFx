package me.leon

import kotlin.test.assertEquals
import me.leon.controller.EncodeController
import me.leon.ext.EncodeType
import org.junit.Before
import org.junit.Test

class EncodeTest {

    lateinit var controller: EncodeController

    @Before
    fun setUp() {
        controller = EncodeController()
    }

    @Test
    fun encode() {
        val raw = "开发工具集合 by leon406@52pojie.cn"

        val base64 = "5byA5Y+R5bel5YW36ZuG5ZCIIGJ5IGxlb240MDZANTJwb2ppZS5jbg=="
        assertEquals(base64, controller.encode2String(raw, EncodeType.Base64))
        assertEquals(raw, controller.decode2String(base64, EncodeType.Base64))

        val base32 = "4W6IBZMPSHS3PJPFQW36TG4G4WIIQIDCPEQGYZLPNY2DANSAGUZHA33KNFSS4Y3O"
        assertEquals(base32, controller.encode2String(raw, EncodeType.Base32))
        assertEquals(raw, controller.decode2String(base32, EncodeType.Base32))

        val base16 =
            "E5BC80E58F91E5B7A5E585B7E99B86E59088206279206C656F6E343036403532706F6A69652E636E"
        assertEquals(base16, controller.encode2String(raw, EncodeType.Base16))
        assertEquals(raw, controller.decode2String(base16, EncodeType.Base16))

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
    }
}
