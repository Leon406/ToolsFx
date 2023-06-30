package me.leon.translate

data class IcibaVocabulary(
    val phrase: List<Phrase>?,
    val derivation: List<Derivation>?,
    val baesInfo: BaesInfo?,
) {

    fun phraseInfo() = phrase?.joinToString(System.lineSeparator()) { it.info() }.orEmpty()

    data class BaesInfo(
        val word_name: String?,
        val is_CRI: String?,
        val exchange: Exchange?,
        val symbols: List<Symbol?>?
    ) {
        fun info(): String {
            println(this)
            return ("$word_name\t" + pronunciation() + "\n" + exchange?.info + "\n\n" + meanings())
        }

        fun pronunciation() = symbols?.get(0)?.run { "UK: /$ph_en/  US: /$ph_am/" }

        fun meanings(separator: String = System.lineSeparator()) =
            symbols?.get(0)?.parts?.run {
                joinToString(separator) { "${it.part} ${it.means?.joinToString("；")}" }
            }

        data class Symbol(
            val ph_en: String,
            val ph_am: String,
            val ph_other: String?,
            val ph_en_mp3: String?,
            val ph_am_mp3: String?,
            val ph_tts_mp3: String?,
            val ph_en_mp3_bk: String?,
            val ph_am_mp3_bk: String?,
            val ph_tts_mp3_bk: String?,
            val parts: List<Part>?
        ) {
            data class Part(val part: String?, val means: List<String?>?)
        }
    }

    data class Phrase(val cizu_name: String, val jx: List<Jx>) {
        data class Jx(val jx_en_mean: String, val jx_cn_mean: String, val lj: List<Lj>?) {
            data class Lj(val lj_ly: String?, val lj_ls: String?)
        }

        fun info() = cizu_name + ": ${jx.joinToString("; ") { it.jx_cn_mean }}"
    }

    data class Derivation(val yuyuan_name: String?)

    data class Exchange(
        val word_pl: List<String>?,
        val word_third: List<String>?,
        val word_past: List<String>?,
        val word_done: List<String>?,
        val word_ing: List<String>?,
        val word_adj: List<String>?
    ) {
        val info
            get() =
                "pl/third/past/done/ing/adj: ${word_pl?.joinToString(",") ?: "-"} " +
                    "${word_third?.joinToString(",") ?: "-"} " +
                    "${word_past?.joinToString(",") ?: "-"} " +
                    "${word_done?.joinToString(",") ?: "-"} " +
                    "${word_ing?.joinToString(",") ?: "-"} " +
                    "${word_adj?.joinToString(",") ?: "-"} "
    }

    override fun toString(): String {
        val p =
            with(phraseInfo()) {
                if (isNotEmpty()) {
                    "\n\n词组:\n\n$this"
                } else {
                    ""
                }
            }
        val origin =
            with(derivation?.get(0)?.yuyuan_name) {
                if (isNullOrEmpty()) {
                    ""
                } else {
                    "\n\n来历:\n\n$this"
                }
            }
        return baesInfo?.info().orEmpty() + p + origin
    }
}
