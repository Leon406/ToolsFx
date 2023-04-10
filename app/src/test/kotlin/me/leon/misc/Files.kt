package me.leon.misc

/**
 * @author Leon
 * @since 2023-04-10 10:31
 * @email deadogone@gmail.com
 */
object Files {
    fun readResourceText(path: String): String =
        Files.javaClass.getResourceAsStream(path)?.reader()?.readText().orEmpty()
}
