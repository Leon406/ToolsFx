package me.leon.ctf

import java.security.Security
import kotlin.test.assertEquals
import me.leon.C1
import me.leon.P1
import me.leon.ext.crypto.ClassicalCryptoType
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test

/** for PBE process comprehend */
class BuddhaTest {

    @Test
    fun buddha() {
        "与佛论禅666".buddhaSays().also { assertEquals("与佛论禅666", it.buddhaExplain()) }
        assertEquals("与佛论禅666", "佛曰：冥耶以缽醯以梵蘇心缽參哆能哆他多罰姪實悉那遮奢三".buddhaExplain())

        Security.addProvider(BouncyCastleProvider())
        val pass = "TakuronDotTop"
        val data = "helloa a "
        val en = data.buddhaPbe(pass)
        println(en)
        assertEquals(data, en.buddhaPbeDecrypt(pass))

        val params = mapOf(P1 to "", C1 to "true")
        val encrypt = ClassicalCryptoType.BuddhaSay.encrypt(data, params)
        assertEquals(data, ClassicalCryptoType.BuddhaSay.decrypt(encrypt, params))
    }
}
