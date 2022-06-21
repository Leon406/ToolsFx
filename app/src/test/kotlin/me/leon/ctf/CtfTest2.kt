package me.leon.ctf

import java.io.File
import me.leon.TEST_CTF_DIR
import org.junit.Test

class CtfTest2 {
    @Test
    fun dnaDecode() {
        File(TEST_CTF_DIR, "dna.txt").readText().dnaDecode().also {
            println(it)
            println(it.dna())
        }
        println(dnaMap)
    }
}
