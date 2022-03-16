package me.leon

import java.io.File
import java.io.Reader
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object Nashorn {
    private val jsEngine: ScriptEngine = ScriptEngineManager().getEngineByName("nashorn")

    fun loadStream(reader: Reader): Nashorn {
        jsEngine.eval(reader)
        return this
    }

    fun loadResource(path: String) = loadStream(javaClass.getResourceAsStream(path).reader())

    fun loadFile(file: File) = loadStream(file.reader())

    fun invoke(func: String, vararg args: Any): Any? {
        return (jsEngine as Invocable).invokeFunction(func, *args)
    }
}
