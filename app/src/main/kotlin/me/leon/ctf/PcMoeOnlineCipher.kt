package me.leon.ctf

import me.leon.ext.*

/** http://hi.pcmoe.net/ */
object PcMoeOnlineCipher {
    private const val PCMOE_URL = "http://hi.pcmoe.net/bear.php"
    const val Buddha = "Buddha"
    const val Bear = "Bear"
    const val Roar = "Roar"

    var xToken: String = ""

    init {
        with("http://hi.pcmoe.net/js/main.min.js".readFromNet()) {
            "function +getKey\\([^)]+\\) *\\{[^}]+}".toRegex().find(this)?.let {
                Nashorn.loadString(it.value)
            }
            """getKey\(\[(\d+(?:,(?:\r\n|\n|\r)?\d+)+)\]\)""".toRegex().find(this)?.let {
                xToken = Nashorn.invoke("getKey", it.groupValues[1].splitByNonDigit()) as String
                println("pcmoe xToken $xToken")
            }
        }
    }

    fun encrypt(mode: String, text: String) = request(mode, true, text)

    fun decrypt(mode: String, text: String) = request(mode, false, text)

    private fun request(mode: String, isEncode: Boolean, data: String) =
        PCMOE_URL.readBytesFromNet(
                "POST",
                data =
                    mapOf(
                            "mode" to mode,
                            "code" to if (isEncode) "isEncode" else "Decode",
                            "txt" to data
                        )
                        .toParams(),
                headers =
                    mapOf(
                        "X-Requested-With" to "XMLHttpRequest",
                        "X-Token" to xToken,
                        "Content-type" to "application/x-www-form-urlencoded",
                        "Referer" to "http://hi.pcmoe.net/index.html",
                    )
            )
            .decodeToString()
}
