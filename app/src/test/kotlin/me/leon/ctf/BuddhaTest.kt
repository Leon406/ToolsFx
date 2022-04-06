package me.leon.ctf

import kotlin.test.assertEquals
import org.junit.Test

/** for PBE process comprehend */
class BuddhaTest {

    @Test
    fun buddha() {
        "与佛论禅666".buddhaSays().also { assertEquals("与佛论禅666", it.buddhaExplain()) }
        assertEquals("与佛论禅666", "佛曰：冥耶以缽醯以梵蘇心缽參哆能哆他多罰姪實悉那遮奢三".buddhaExplain())
    }
}
