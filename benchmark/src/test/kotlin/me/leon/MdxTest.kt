package me.leon

import java.io.File
import org.jsoup.Jsoup

/**
 * @author Leon
 * @since 2023-06-30 14:10
 * @email deadogone@gmail.com
 */
fun main() {
    println(System.getProperty("user.dir"))
    val dict = MdictLoader.loadMdx(MDX)
    val tnew = File("$DESKTOP/tnew2.txt")
    val err = File("$DESKTOP/err.txt")
    val nodata = File("$DESKTOP/nodata.txt")
    nodata.writeText("")
    if (!tnew.exists()) {
        tnew.createNewFile()
    }
    val coca = File("$DESKTOP/nomeaning.txt").readText().lines()

    val errors = mutableSetOf<String>()
    val untranslated = coca.exclude(tnew.parseWordKv().map { it.first })
    //        .filterNot {
    //
    // it.contains("-(old|year|day|century|hour|liter|dollar|inch|week|minute|ton|long|like|tall)|(top|mid|early|post|pre|anti)-".toRegex()) || it.contains(
    //            "/"
    //        )
    //    }
    //    val untranslated = listOf("zinfandel")
    println("$tnew/${untranslated.size}/${coca.size}")
    dict.run {
        for (word in untranslated) {
            var pw = word.lowercase().split("-").joinToString("-") { it.capitalize() }
            println(">>> $pw")
            var articles = readArticles(pw)
            if (articles.isEmpty()) {
                pw = word.lowercase()
                println(">>> $pw")
                articles = readArticles(pw)
            }
            if (articles.isEmpty()) {
                pw = word.uppercase()
                println(">>> $pw")
                articles = readArticles(pw)
            }

            if (articles.isEmpty()) {
                println(">>><<<")
                errors.add(word)
                continue
            }
            for (article in articles) {
                runCatching {
                        val mean = parse(article.value)
                        if (mean.isNotEmpty()) {
                            val data = article.key.lowercase() + "\t" + mean
                            tnew.appendText(data)
                            tnew.appendText("\n")
                        } else {
                            nodata.appendText("$word${System.lineSeparator()}")
                        }
                    }
                    .getOrElse { errors.add(word) }
            }
        }
    }

    println("~~~~~~~~~~~~~~~~~~")
    println(errors.joinToString(";"))
    err.writeText(errors.joinToString(System.lineSeparator()))
}

fun File.parseWordKv() =
    readText().lines().map {
        val p = it.split("\t")
        p.first() to p.last()
    }

fun List<String>.exclude(another: List<String>) = filterNot {
    another.contains(it) ||
        another.contains(it.capitalize()) ||
        another.contains(it.uppercase()) ||
        (it.contains("-") && another.contains(it.split("-").joinToString("-") { it.capitalize() }))
}

fun parse(html: String) =
    Jsoup.parse(html).run {
        val elements = select(".dcb")
        if (elements.isEmpty()) {
            ""
        } else {
            elements.joinToString(" ") { it.text().replace(".", ". ") }
        }
    }
