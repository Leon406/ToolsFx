package me.leon.translate

import com.google.gson.annotations.SerializedName

/**
 * @author Leon
 * @since 2023-06-29 9:25
 * @email deadogone@gmail.com
 */
@Suppress("All")
data class YouDaoResponse(
    @SerializedName("web_trans") val webTrans: WebTrans?,
    val simple: Simple?,
    val phrs: Phrs?,
    val lang: String,
    val ec: Ec?,
    @SerializedName("rel_word") val relWord: RelWord?,
    val etym: Etym?
) {

    fun simple(): String = buildString {
        if (simple?.query != null && ec?.means() != null) {
            append(
                ec.means()
                    .replace("\n", "")
                    .replace("\r\n", "")
                    .replace("; · [^;]+; [^;\\r]+".toRegex(), "")
                    .replace("。;", ";")
                    .replace("．;", "．")
            )
        }
    }

    override fun toString(): String {
        return buildString {
            append(simple)
            ec?.run { append(this) }
            appendLine()
            webTrans?.run { append(this) }

            phrs?.run {
                append("\n\n词组搭配: \n\n")
                append(this)
            }
            relWord?.run {
                append("\n\n派生: \n\n")
                append(this)
            }
            etym?.run {
                append("\n\n词源: \n\n")
                append(this)
            }
        }
    }
}

data class WebTrans(@SerializedName("web-translation") val webTranslation: List<WebTranslation>) {

    override fun toString(): String {
        return webTranslation.joinToString(System.lineSeparator()) { it.translation() }
    }
}

data class Simple(val query: String, val word: List<Word>) {
    override fun toString(): String {
        return buildString {
            append(query).append("\t ")
            word.firstOrNull()?.run {
                usphone?.let { append("美 $it ") }
                ukphone?.let { append("英 $it") }
            }
        }
    }
}

data class Phrs(val word: String, val phrs: List<Phr>) {

    override fun toString(): String {
        return phrs.joinToString("\n")
    }

    data class Phr(val phr: PhrX) {

        override fun toString(): String {
            return phr.toString()
        }

        data class PhrX(val headword: Headword, val trs: List<Tr>, val source: String) {

            override fun toString(): String {
                return "$headword\t\t${trs.joinToString("\t") { it.tr.toString() }}"
            }

            data class Tr(val tr: Headword)
        }
    }
}

data class Ec(val exam_type: List<String>, val word: List<WordX>) {

    fun means() =
        word
            .firstOrNull()
            ?.trs
            //        ?.filter {
            //        it.tr.first().l.i.any { it.contains("^\\w+".toRegex()) } ?: true
            //    }
            ?.joinToString("; ")
            .orEmpty()

    override fun toString(): String {
        return buildString {
            append(exam_type.joinToString("/"))
            appendLine()
            append(word.firstOrNull()?.toString().orEmpty())
        }
    }
}

data class RelWord(val word: String, val stem: String, val rels: List<Rel>) {

    override fun toString(): String {
        return rels.joinToString("\n\n")
    }

    data class Rel(val rel: RelX) {

        override fun toString(): String {
            return rel.toString()
        }

        data class RelX(val pos: String, val words: List<WordXX>) {
            data class WordXX(val word: String, val tran: String) {
                override fun toString(): String {
                    return "$word\t$tran"
                }
            }

            override fun toString(): String {
                return "$pos\n${words.joinToString(System.lineSeparator())}"
            }
        }
    }
}

data class Etym(val etyms: Etyms, val word: String) {
    override fun toString(): String {
        return etyms.toString()
    }

    data class Etyms(val zh: List<Zh>) {
        override fun toString(): String {
            return zh.joinToString("\n\n") { it.toString() }
        }

        data class Zh(
            val source: String,
            val word: String,
            val value: String,
            val url: String,
            val desc: String
        ) {
            override fun toString(): String {
                return buildString {
                    append(desc).appendLine()
                    append(value)
                }
            }
        }
    }
}

data class WebTranslation(val key: String, val `key-speech`: String, val trans: List<Tran>) {
    fun translation() = buildString {
        append(key)
        append("\t ")
        append(trans.joinToString(";") { it.value })
    }
}

data class Tran(val summary: Summary?, val value: String, val support: Int?, val url: String?)

data class Summary(val line: List<String>)

data class Word(
    val usphone: String?,
    val ukphone: String?,
)

data class Headword(val l: L) {
    data class L(val i: String)

    override fun toString(): String {
        return l.i
    }
}

data class WordX(
    val usphone: String,
    val ukphone: String,
    val trs: List<TrXX>,
    val wfs: List<Wf>,
) {

    override fun toString(): String {
        return buildString {
            //            append("美: $usphone 英: $ukphone")
            appendLine()
            append(trs.joinToString(System.lineSeparator()))
            appendLine()
            appendLine()
            append(wfs.joinToString(" "))
        }
    }

    data class TrXX(val tr: List<TrXXX>) {

        override fun toString(): String {
            return tr.joinToString(System.lineSeparator())
        }

        data class TrXXX(val l: LXX) {
            data class LXX(val i: List<String>) {
                override fun toString(): String {
                    return i.joinToString(";")
                }
            }

            override fun toString(): String {
                return l.toString()
            }
        }
    }

    data class Wf(val wf: WfX) {
        data class WfX(val name: String, val value: String) {
            override fun toString(): String {
                return "$name: $value"
            }
        }

        override fun toString(): String {
            return wf.toString()
        }
    }
}
