package me.leon.plugin.jna

import com.sun.jna.*
import com.sun.jna.win32.StdCallLibrary
import org.junit.Test

/**
 * 环境变量配置 PATH Windows LD_LIBRARY_PATH Linux DYLD_LIBRARY_PATH OSX
 *
 * class path
 */
interface CLibrary : Library {
    fun printf(format: String?, vararg args: Any?)

    companion object {
        val INSTANCE =
            Native.load(if (Platform.isWindows()) "msvcrt" else "c", CLibrary::class.java)
                as CLibrary
    }
}

@Suppress("TestFunctionName")
interface Kernel32 :
    StdCallLibrary { // Method declarations, constant and structure definitions go here
    fun GetSystemTime(result: SystemTime?)
}

class JniTest {
    @Test
    fun jni() {
        CLibrary.INSTANCE.printf("Hello, World\n")
        val args = listOf("a", "b", "c")
        args.forEachIndexed { i, s -> CLibrary.INSTANCE.printf("Argument %d: %s\n", i, s) }
    }

    @Test
    fun kernel32() {
        val lib = Native.load("kernel32", Kernel32::class.java) as Kernel32
        val time = SystemTime()
        lib.GetSystemTime(time)

        println("Today's integer value is " + time.wDay)
        println("Today's integer value is " + time.wMonth)
    }
}
