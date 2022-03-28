package me.leon.ext

object JavascriptCipher {
    private val nashorn: Nashorn by lazy {
        Nashorn.loadResource("/js/aaencode.js")
            .loadResource("/js/jjencode.js")
            .loadResource("/js/rabbit.js")
    }

    fun aaEncode(s: String) = nashorn.invoke("aaencode", s) as String

    fun aaDecode(s: String) = nashorn.invoke("aadecode", s) as String

    fun jjEncode(s: String, globalVar: String = "$", p: Boolean = false) =
        nashorn.invoke("encode_jj", s, globalVar, p) as String

    fun jjDecode(s: String) = nashorn.invoke("jjdecode", s) as String

    fun rabbitEncrypt(s: String, pwd: String = "") =
        nashorn.invoke("rabbitEncrypt", s, pwd) as String

    fun rabbitDecrypt(s: String, pwd: String = "") =
        nashorn.invoke("rabbitDecrypt", s, pwd) as String
}
