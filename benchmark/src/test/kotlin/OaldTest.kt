import kotlin.test.Test
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-08-07 16:28
 * @email deadogone@gmail.com
 */
class OaldTest {

    @Test
    fun newWord() {

        val words = mutableMapOf<String, List<String>>()
        Jsoup.connect("https://www.oxfordlearnersdictionaries.com/us/wordlist/new_words")
            .get()
            .also {
                it.select(".open-close>dt").also {
                    println(
                        it.joinToString(System.lineSeparator()) {
                            words[it.text().substringAfter("in ")] = mutableListOf()
                            it.text()
                        }
                    )
                }
                println("~~~~~~~~~~~")
                it.select(".open-close>dd").also {
                    println(
                        it.joinToString(System.lineSeparator()) {
                            it.select("a").joinToString() { it.text() }
                        }
                    )
                }
            }
    }

    @Test
    fun body() {
        val word = "asleep"
        val css =
            "<link href=\"https://www.oxfordlearnersdictionaries.com/us/external/styles/oald10.css" +
                    "?version=2.3.50\" rel=\"stylesheet\" type=\"text/css\">"
        Jsoup.connect("https://www.oxfordlearnersdictionaries.com/us/definition/english/$word")
            .get()
            .also {
                //                println(it)
                val content = it.selectFirst("#entryContent")
                content?.getElementById("ring-links-box")?.remove()
                content?.select(".xh")?.remove()
                content
                    ?.select(
                        ".dictlink-g,.pron-link,.responsive_display_inline_on_smartphone,.xref_to_full_entry"
                    )
                    ?.remove()
                content?.select(".Ref")?.forEach {
                    val href = it.attr("href")
                    val startIndex = href.indexOf('#')
                    if (startIndex > 0) {
                        println(it.attr("href", href.substring(startIndex)))
                    }
                }
                println(content.toString().replace("(?<=>)\\s+(?=<)|\r\n|\n".toRegex(), ""))
            }
    }
}
