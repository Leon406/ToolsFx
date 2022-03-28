package me.leon

import me.leon.ctf.PcMoeOnlineCipher
import org.junit.Test

class OnlineRequestTest {
    @Test
    fun pcmoe() {
        PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Buddha, "你好Leon406").also {
            println(it)
            println(PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Buddha, it))
        }

        PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Roar, "你好Leon406").also {
            println(it)
            println(PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Roar, it))
        }

        PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Bear, "你好Leon406").also {
            println(it)
            println(PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Bear, it))
        }
    }
}
