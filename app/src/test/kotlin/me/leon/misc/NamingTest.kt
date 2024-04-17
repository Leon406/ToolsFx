package me.leon.misc

import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.Test

/**
 * @author Leon
 * @since 2024-04-17 11:09
 * @email deadogone@gmail.com
 */
class NamingTest {

    private val raw = "hello leon"

    @Test
    fun test() {

        var list =
            setOf(
                "helloLeon",
                "HelloLeon",
                "hello_leon",
                "HELLO_LEON",
                "hello-leon",
                "HELLO-LEON",
                "hello.leon",
                "leon.hello",
                "Hello leon",
                "Hello Leon",
                "HELLO Leon",
            )

        list = list.map { it.normalSpaceFormat() }.toSet()

        list.forEach { println(it.lowerCamelNaming()) }
        list.forEach { println(it.upperCamelNaming()) }
        list.forEach { println(it.uppercaseUnderscoreNaming()) }
        list.forEach { println(it.underscoreNaming()) }
        list.forEach { println(it.dashNaming()) }
        list.forEach { println(it.uppercaseDashNaming()) }
        list.forEach { println(it.packageNaming()) }
        list.forEach { println(it.packageReverseNaming()) }
        list.forEach { println(it.titleNaming()) }
        list.forEach { println(it.firstWordUppercaseNaming()) }
    }

    fun String.normalSpaceFormat(): String =
        (if (filter { it.isLetter() }.all { it.isUpperCase() } || contains(' ')) {
                lowercase()
            } else {
                this
            })
            .replace(".", " ")
            .replace("-", " ")
            .replace("_", " ")
            .map {
                if (it.isUpperCase()) {
                    " $it"
                } else {
                    it
                }
            }
            .joinToString("")
            .replace("  ", " ")
            .trim()
            .lowercase()

    fun String.lowerCamelNaming() = charTransform(Char::uppercase)
    // pascal case
    fun String.upperCamelNaming() = with(lowerCamelNaming()) { first().uppercase() + drop(1) }

    fun String.charTransform(action: (Char) -> String) =
        normalSpaceFormat()
            .fold(StringBuilder() to AtomicBoolean()) { acc, char ->
                if (char == ' ') {
                    acc.second.set(true)
                } else {
                    if (acc.second.get()) {
                        acc.first.append(action(char))
                        acc.second.set(false)
                    } else {
                        acc.first.append(char)
                    }
                }
                acc
            }
            .first
            .toString()

    // snake case
    fun String.underscoreNaming() = normalSpaceFormat().replace(" ", "_")
    // upper case
    fun String.uppercaseUnderscoreNaming() = underscoreNaming().uppercase()
    // kebab case
    fun String.dashNaming() = normalSpaceFormat().replace(" ", "-")

    fun String.uppercaseDashNaming() = dashNaming().uppercase()

    /** first word capitalized */
    fun String.titleNaming() =
        normalSpaceFormat().split(" ").joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

    /** on */
    fun String.firstWordUppercaseNaming() = normalSpaceFormat().first().uppercase() + drop(1)

    fun String.packageNaming() = normalSpaceFormat().replace(" ", ".")

    fun String.packageReverseNaming() = normalSpaceFormat().split(" ").reversed().joinToString(".")
}
