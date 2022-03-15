package me.leon

import me.leon.ctf.buddhaExplain
import me.leon.ctf.buddhaSays
import kotlin.test.assertEquals
import org.junit.Test

/** for PBE process comprehend */
class BuddhaTest {

    @Test
    fun buddha() {
        "与佛论禅666".buddhaSays().also {
            println(it)
            assertEquals("与佛论禅666", it.buddhaExplain())
        }
        "佛曰：冥耶以缽醯以梵蘇心缽參哆能哆他多罰姪實悉那遮奢三".buddhaExplain().also { println(it) }
    }
}
