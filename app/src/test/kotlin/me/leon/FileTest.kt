package me.leon

import me.leon.ext.toFile
import org.junit.Test

class FileTest {
    @Test
    fun gradleCacheJarToM2() {
        val home = System.getenv("USERPROFILE")
        println(home)
        val gradleJarsDir = "$home\\.gradle\\caches\\modules-2\\files-2.1\\"
        val m2Dir = "$home\\.m2\\repository"

        gradleJarsDir.toFile().walk().filter { it.isFile }.forEach {
            val path =
                it.absolutePath.replace(gradleJarsDir, "").replace("""\\\w{40}""".toRegex(), "")
            val properPath =
                path.substringBefore("\\").replace(".", "\\") + "\\" + path.substringAfter("\\")
            val dstFile = (m2Dir + "\\" + properPath).toFile()
            if (dstFile.exists().not()) {
                println("copy: ${dstFile.absolutePath}")
                //                it.copyTo(dstFile)
                //                it.renameTo(dstFile)
            }
        }
    }
}
