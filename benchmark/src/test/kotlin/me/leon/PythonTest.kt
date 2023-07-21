package me.leon

import org.bytedeco.cpython.global.python.*

/**
 * @author Leon
 * @since 2023-07-20 18:03
 * @email deadogone@gmail.com
 */
class PythonTest

fun main() {
    val program = Py_DecodeLocale(PythonTest::class.java.getSimpleName(), null)
    if (program == null) {
        System.err.println("Fatal error: cannot decode class name")
        System.exit(1)
    }
    Py_SetProgramName(program) /* optional but recommended */
    println(cachePackages().contentToString())
    Py_Initialize(*cachePackages())
    PyRun_SimpleString(
        """
                from time import time,ctime
                print('Today is', ctime(time()))
                
                """
            .trimIndent()
    )
    if (Py_FinalizeEx() < 0) {
        System.exit(120)
    }
    PyMem_RawFree(program)
    System.exit(0)
}
