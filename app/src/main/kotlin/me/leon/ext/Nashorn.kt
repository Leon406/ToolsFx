package me.leon.ext

import java.io.File
import java.io.Reader
import javax.script.*

object Nashorn {
    private val jsEngine: ScriptEngine = ScriptEngineManager().getEngineByName("nashorn")

    fun loadStream(reader: Reader): Nashorn {
        jsEngine.eval(reader)
        return this
    }

    fun loadString(script: String): Nashorn {
        jsEngine.eval(script)
        return this
    }

    fun loadResource(path: String) = loadStream(javaClass.getResourceAsStream(path).reader())

    fun loadFile(file: File) = loadStream(file.reader())

    fun invoke(func: String, vararg args: Any): Any? {
        return (jsEngine as Invocable).invokeFunction(func, *args)
    }
}
