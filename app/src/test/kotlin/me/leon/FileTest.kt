package me.leon

import java.io.File
import kotlin.test.Ignore
import me.leon.ext.toFile
import org.junit.Test

class FileTest {
    @Test
    @Ignore
    fun gradleCacheJarToM2() {
        val home = System.getenv("USERPROFILE")
        println(home)
        val gradleJarsDir = "$home\\.gradle\\caches\\modules-2\\files-2.1\\"
        val m2Dir = "$home\\.m2\\repository"

        gradleJarsDir
            .toFile()
            .walk()
            .filter { it.isFile }
            .forEach {
                val path =
                    it.absolutePath
                        .replace(gradleJarsDir, "")
                        .replace("""\\\w{38,}""".toRegex(), "")
                val properPath =
                    path.substringBefore("\\").replace(".", "\\") + "\\" + path.substringAfter("\\")
                val dstFile = (m2Dir + "\\" + properPath).toFile()
                if (dstFile.exists().not()) {
                    println("copy: ${dstFile.absolutePath}")
                    //                it.copyTo(dstFile)
                    dstFile.parentFile.mkdirs()

                    val renameState = it.renameTo(dstFile)
                    if (!renameState) {
                        println("rename failed: ${dstFile.absolutePath}")
                        it.copyTo(dstFile)
                    }
                } else {
                    val delete = it.delete()
                    println("exist: $it $delete")
                }
            }
    }

    @Test
    fun deleteEmptyDir() {
        "E:\\ttt".toFile().deleteEmptyDirs()
    }

    fun File.deleteEmptyDirs() {
        if (!exists() || !isDirectory) {
            return
        }

        val files = listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                file.deleteEmptyDirs()
            }
        }
        if (listFiles()?.isEmpty() == true) {
            delete()
        }
    }
}
