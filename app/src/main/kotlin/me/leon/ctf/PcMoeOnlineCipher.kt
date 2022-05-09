package me.leon.ctf

import me.leon.ext.readBytesFromNet

/** http://hi.pcmoe.net/ */
object PcMoeOnlineCipher {
    private const val PCMOE_URL = "http://hi.pcmoe.net/bear.php"
    const val Buddha = "Buddha"
    const val Bear = "Bear"
    const val Roar = "Roar"

    fun encrypt(mode: String, text: String) = request(mode, true, text)

    fun decrypt(mode: String, text: String) = request(mode, false, text)

    private fun request(mode: String, isEncode: Boolean, data: String) =
        PCMOE_URL
            .readBytesFromNet(
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
                        "X-Token" to "203B61D35068",
                        "Content-type" to "application/x-www-form-urlencoded",
                        "Referer" to "http://hi.pcmoe.net/index.html",
                    )
            )
            .decodeToString()

    private fun Map<String, Any>.toParams() = entries.joinToString("&") { it.key + "=" + it.value }
}
