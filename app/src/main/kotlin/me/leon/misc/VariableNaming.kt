package me.leon.misc

import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Leon
 * @since 2024-04-18 8:38
 * @email deadogone@gmail.com
 */
fun String.normalSpaceFormat(): String =
    (if (
            filter { it.isUpperCase() || it.isLowerCase() }.all { it.isUpperCase() } ||
                contains(' ')
        ) {
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

/** （小）驼峰 hellLeon */
fun String.camelNaming() = charTransform(Char::uppercase)
// pascal case

/** 帕斯卡命名 （大）驼峰 HellLeon */
fun String.pascalNaming() = with(camelNaming()) { first().uppercase() + drop(1) }

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

/** 蛇形命名，下划线命名 hello_leon */
fun String.snakeCaseNaming() = normalSpaceFormat().replace(" ", "_")

/** 大写命名 HELLO_LEON, 常量 */
fun String.uppercaseNaming() = snakeCaseNaming().uppercase()
//
/** kebab case， spinal case */
fun String.dashNaming() = normalSpaceFormat().replace(" ", "-")

fun String.uppercaseDashNaming() = dashNaming().uppercase()

/** first word capitalized */
fun String.titleNaming() =
    normalSpaceFormat().split(" ").joinToString(" ") {
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

fun String.firstWordUppercaseNaming() = with(normalSpaceFormat()) { first().uppercase() + drop(1) }

fun String.packageNaming() = normalSpaceFormat().replace(" ", ".")

fun String.packageReverseNaming() = normalSpaceFormat().split(" ").reversed().joinToString(".")

enum class VariableNaming(val func: String.() -> String) {

    CamelCase(String::camelNaming),
    Pascal(String::pascalNaming),
    SnakeCase(String::snakeCaseNaming),
    UpperCase(String::uppercaseNaming),
    Dash(String::dashNaming),
    UpperDash(String::uppercaseDashNaming),
    Title(String::titleNaming),
    FirstWordUpper(String::firstWordUppercaseNaming),
    Package(String::packageNaming),
    PackageReverse(String::packageReverseNaming);

    fun convert(s: String) = func(s)
}
