package me.leon.ext.crypto

import me.leon.ext.Nashorn

object JavascriptCipher {
    init {
        Nashorn.loadResource("/js/aaencode.js")
            .loadResource("/js/jjencode.js")
            .loadResource("/js/rabbit.js")
    }

    fun aaEncode(s: String) = Nashorn.invoke("aaencode", s) as String

    fun aaDecode(s: String) = Nashorn.invoke("aadecode", s) as String

    fun jjEncode(s: String, globalVar: String = "$", p: Boolean = false) =
        Nashorn.invoke("encode_jj", s, globalVar, p) as String

    fun jjDecode(s: String) = Nashorn.invoke("jjdecode", s) as String

    fun rabbitEncrypt(s: String, pwd: String = "") =
        Nashorn.invoke("rabbitEncrypt", s, pwd) as String

    fun rabbitDecrypt(s: String, pwd: String = "") =
        Nashorn.invoke("rabbitDecrypt", s, pwd) as String
}
